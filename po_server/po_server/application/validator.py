import datetime
import re

REGISTER_KEYS = ['mail', 'haslo', 'imie', 'nazwisko', 'data_urodzenia']
LOGIN_KEYS = ['mail', 'haslo']
ADD_PATH_KEYS = ['mail', 'haslo', 'grupa_gorska', 'punktacja', 'punktacja_odwrotnie', 'punkt_startowy', 'punkt_koncowy']
SINGLE_TOUR_KEYS = ['mail', 'haslo', 'trasa_id']

regex_mail = '\w+[@][\w\.]+[\.]\w{2,3}'


def get_string_date_from_date(date):
    try:
        selected_date = datetime.datetime.strptime(date, '%Y-%m-%d').date()
        string_date = selected_date.strftime("%Y-%m-%d")
        return string_date
    except ValueError:
        return ""


def is_register_json_valid(json_dict):
    if json_dict is None:
        return False
    if len(REGISTER_KEYS) != len(json_dict):
        return False

    for valid_key in REGISTER_KEYS:
        if valid_key not in json_dict.keys():
            return False

    for valid_key in REGISTER_KEYS:
        if len(str(json_dict[valid_key])) == 0:
            return False

    string_date = get_string_date_from_date(json_dict['data_urodzenia'])
    if "" == string_date:
        return False

    if not re.search(regex_mail, json_dict['mail']):
        return False

    return True


def is_login_json_valid(json_dict):
    if json_dict is None:
        return False
    if len(LOGIN_KEYS) != len(json_dict):
        return False

    for valid_key in LOGIN_KEYS:
        if valid_key not in json_dict.keys():
            return False

    for valid_key in LOGIN_KEYS:
        if len(str(json_dict[valid_key])) == 0:
            return False

    return True


def is_add_path_json_valid(json_dict):
    if json_dict is None:
        return False
    if len(ADD_PATH_KEYS) != len(json_dict):
        return False

    for valid_key in ADD_PATH_KEYS:
        if valid_key not in json_dict.keys():
            return False

    for valid_key in ADD_PATH_KEYS:
        if len(str(json_dict[valid_key])) == 0:
            return False

    if json_dict['punkt_startowy'] == json_dict['punkt_koncowy']:
        return False

    if int(json_dict['punktacja']) < 0 or int(json_dict['punktacja_odwrotnie']) < 0:
        return False

    if int(json_dict['punktacja']) > 0 or int(json_dict['punktacja_odwrotnie']) > 0:
        return True

    return False


def is_single_tour_to_confirm(json_dict):
    if json_dict is None:
        return False
    if len(SINGLE_TOUR_KEYS) != len(json_dict):
        return False

    for valid_key in SINGLE_TOUR_KEYS:
        if valid_key not in json_dict.keys():
            return False

    for valid_key in SINGLE_TOUR_KEYS:
        if len(str(json_dict[valid_key])) == 0:
            return False

    return True
