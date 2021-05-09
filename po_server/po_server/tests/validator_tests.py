import unittest
import application.validator as validator


class ValidatorTestCase(unittest.TestCase):
    def test_date_formatting(self):
        # date should be changed to format 'YYYY-MM-DD'
        provided = '2010-2-1'
        expected = '2010-02-01'
        self.assertEqual(validator.get_string_date_from_date(provided), expected)

    def test_date_formatting_wrong_date(self):
        # with wrong date '' is returnd
        provided = '2010-02-30'
        expected = ''
        self.assertEqual(validator.get_string_date_from_date(provided), expected)

    def test_register_json_mail_without_at_sign(self):
        # mail does not contain '@' sign
        register_json_without_at_sign = {'mail': 'NoAtSign', 'haslo': 'password', 'imie': 'name',
                                         'nazwisko': 'lastName',
                                         'data_urodzenia': '2000-01-01'}
        self.assertFalse(validator.is_register_json_valid(register_json_without_at_sign))

    def test_register_json_mail_wrong_format(self):
        # mail does not contain .com (or other domain)
        register_json_without_at_sign = {'mail': 'abcd@gmail', 'haslo': 'password', 'imie': 'name',
                                         'nazwisko': 'lastName',
                                         'data_urodzenia': '2000-01-01'}
        self.assertFalse(validator.is_register_json_valid(register_json_without_at_sign))

    def test_register_json_proper_mail(self):
        register_json_without_at_sign = {'mail': 'abcd@gmail.com', 'haslo': 'password', 'imie': 'name',
                                         'nazwisko': 'lastName',
                                         'data_urodzenia': '2000-01-01'}
        self.assertTrue(validator.is_register_json_valid(register_json_without_at_sign))

    def test_add_path_zero_points_both_ways(self):
        # 'punktacja' and 'punktacja_odwrotnie' can't be zero at the same time
        add_path_both_ways_zero = {'mail': 'abcd@gmail.com', 'haslo': 'password', 'grupa_gorska': 1, 'punktacja': 0,
                                   'punktacja_odwrotnie': 0, 'punkt_startowy': 1, 'punkt_koncowy': 2}
        self.assertFalse(validator.is_add_path_json_valid(add_path_both_ways_zero))

    def test_add_path_zero_one_way(self):
        # 'punktacja' and 'punktacja_odwrotnie' - one zero is acceptable
        add_path_one_way_zero = {'mail': 'abcd@gmail.com', 'haslo': 'password', 'grupa_gorska': 1, 'punktacja': 0,
                                 'punktacja_odwrotnie': 5, 'punkt_startowy': 1, 'punkt_koncowy': 2}
        self.assertTrue(validator.is_add_path_json_valid(add_path_one_way_zero))

    def test_add_path_positive_points(self):
        # 'punktacja' and 'punktacja_odwrotnie' can both be positive
        add_path_both_positive = {'mail': 'abcd@gmail.com', 'haslo': 'password', 'grupa_gorska': 1, 'punktacja': 4,
                                  'punktacja_odwrotnie': 5, 'punkt_startowy': 1, 'punkt_koncowy': 2}
        self.assertTrue(validator.is_add_path_json_valid(add_path_both_positive))

    def test_add_path_negative_points(self):
        # 'punktacja' and 'punktacja_odwrotnie' - neither should be negative
        add_path_negative = {'mail': 'abcd@gmail.com', 'haslo': 'password', 'grupa_gorska': 1, 'punktacja': -4,
                             'punktacja_odwrotnie': 5, 'punkt_startowy': 1, 'punkt_koncowy': 2}
        self.assertFalse(validator.is_add_path_json_valid(add_path_negative))

    def test_login_ok(self):
        # proper login json
        login = {'mail': 'abcd@gmail.com', 'haslo': 'password'}
        self.assertTrue(validator.is_login_json_valid(login))

    def test_login_no_password(self):
        # empty password
        login = {'mail': 'abcd@gmail.com', 'haslo': ''}
        self.assertFalse(validator.is_login_json_valid(login))

    def test_login_no_password_field(self):
        # missing password field
        login = {'mail': 'abcd@gmail.com'}
        self.assertFalse(validator.is_login_json_valid(login))


if __name__ == '__main__':
    unittest.main()
