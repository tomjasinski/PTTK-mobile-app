import os
import sqlite3
import sys

QUERY_SELECT_ALL_GROUPS = """
SELECT * FROM GrupaGorska;
"""

QUERY_SELECT_ID_GRUPA_GORSKA_BY_NAZWA = """
SELECT MAX(id) FROM GrupaGorska WHERE nazwa = ?;
"""

QUERY_SELECT_ALL_POINTS_FROM_GROUP = """
SELECT * FROM Punkt WHERE grupa_gorska_id = ?;
"""

QUERY_SELECT_UZYTKOWNIK_BY_MAIL_AND_PASSWORD = """
SELECT * FROM Uzytkownik WHERE mail = ? AND haslo = ?;
"""

QUERY_SELECT_PUNKT_BY_NAZWA = """
SELECT * FROM Punkt WHERE nazwa = ?;
"""

QUERY_SELECT_PRZODOWNIK_BY_UZYTKOWNIK_ID = """
SELECT * FROM Przodownik WHERE uzytkownik_id = ?;
"""

QUERY_SELECT_ODCINEK_BY_PUNKTY_ID = """
SELECT * FROM Odcinek WHERE punkt_startowy = ? AND punkt_koncowy = ?;
"""

QUERY_SELECT_ALL_PATHS_FROM_GROUP = """
SELECT * FROM Odcinek WHERE punkt_startowy IN (SELECT id FROM Punkt WHERE grupa_gorska_id = ?) 
AND punkt_koncowy IN (SELECT id FROM Punkt WHERE grupa_gorska_id = ?);
"""

QUERY_SELECT_ODCINEK_BY_NAZWY_PUNKTOW = """
SELECT * FROM Odcinek WHERE (punkt_startowy = (SELECT MAX(id) FROM Punkt WHERE nazwa = ?) 
    AND punkt_koncowy = (SELECT MAX(id) FROM Punkt WHERE nazwa = ?))
    OR (punkt_koncowy = (SELECT MAX(id) FROM Punkt WHERE nazwa = ?) 
    AND punkt_startowy = (SELECT MAX(id) FROM Punkt WHERE nazwa = ?));   
"""

QUERY_SELECT_TRASY_DO_POTWIERDZENIA_BY_PRZODOWNIK_ID = """
SELECT U.id "uzytkownik_id", U.imie, U.nazwisko, T.id "trasa_id"
FROM Uzytkownik U JOIN Trasa T ON U.id = T.uzytkownik_id
WHERE T.potwierdzona_przez IS NULL;
"""

QUERY_SELECT_TRASA_INFO_BY_TRASA_ID = """
SELECT data_rozpoczecia, data_zakonczenia
FROM Trasa
WHERE potwierdzona_przez IS NULL
AND id = ?;
"""

QUERY_SELECT_POJEDYNCZA_TRASA_DO_POTWIERDZENIA_BY_TRASA_ID = """
SELECT 
    (SELECT P.nazwa FROM Punkt P WHERE P.id = 
        (CASE CT.czy_odwrotnie WHEN 0 THEN O.punkt_startowy ELSE O.punkt_koncowy END)) "punkt_startowy",
    (SELECT P.nazwa FROM Punkt P WHERE P.id = 
        (CASE CT.czy_odwrotnie WHEN 0 THEN O.punkt_koncowy ELSE O.punkt_startowy END)) "punkt_koncowy",
    CASE CT.czy_odwrotnie WHEN 0 THEN O.punktacja ELSE O.punktacja_odwrotnie END "punktacja",
    CT.liczba_porzadkowa
FROM Odcinek O JOIN CzescTrasy CT ON O.id = CT.odcinek_id
WHERE CT.trasa_id = ?
ORDER BY CT.liczba_porzadkowa;
"""

Q = """
SELECT MAX(nazwa) FROM Punkt WHERE id = 1;
"""

QUERY_SELECT_UZYKOWNIK_BY_MAIL = """
SELECT * FROM Uzytkownik WHERE mail = ?;
"""

QUERY_INSERT_INTO_TABLE_UZYTKOWNIK = """
INSERT INTO Uzytkownik (mail, haslo, imie, nazwisko, data_urodzenia) VALUES (?, ?, ?, ?, ?);
"""

QUERY_INSERT_INTO_ODCINEK = """
INSERT INTO Odcinek (punkt_startowy, punkt_koncowy, punktacja, punktacja_odwrotnie) 
VALUES (?, ?, ?, ?);
"""

QUERY_UPDATE_TRASA = """
UPDATE TRASA
SET potwierdzona_przez = ?
WHERE id = ?;
"""

QUERY_DELETE_TRASA = """
DELETE FROM Trasa WHERE id = ?;
"""

GOT_SQLITE = '../GOT.sqlite'


def get_result_from_query(query, parameter):
    connection = sqlite3.connect(GOT_SQLITE)
    cursor = connection.cursor()

    cursor.execute(query, parameter)
    result = cursor.fetchall()

    cursor.close()
    connection.close()

    return result


def execute(query, parameters):
    connection = sqlite3.connect(GOT_SQLITE)
    cursor = connection.cursor()

    cursor.execute(query, parameters)

    connection.commit()
    cursor.close()
    connection.close()


def is_valid_for_register(json_dict):
    result = get_result_from_query(QUERY_SELECT_UZYKOWNIK_BY_MAIL, [json_dict['mail']])
    if len(result) == 0:
        return True
    else:
        return False


def insert_in_uzytkownik(json_dict):
    execute(QUERY_INSERT_INTO_TABLE_UZYTKOWNIK,
            [json_dict['mail'], json_dict['haslo'], json_dict['imie'], json_dict['nazwisko'],
                    json_dict['data_urodzenia']])
    result = get_result_from_query(QUERY_SELECT_UZYKOWNIK_BY_MAIL, [json_dict['mail']])
    return result[0][0]


def get_points_from_group(group_id):
    return get_result_from_query(QUERY_SELECT_ALL_POINTS_FROM_GROUP, [group_id])


def get_paths_from_group(group_id):
    return get_result_from_query(QUERY_SELECT_ALL_PATHS_FROM_GROUP, [group_id, group_id])


def log_in(json_dict):
    result = get_result_from_query(QUERY_SELECT_UZYTKOWNIK_BY_MAIL_AND_PASSWORD,
                                   [json_dict['mail'], json_dict['haslo']])
    if len(result) > 0:
        return result[0][0]
    else:
        return -1


def is_przodownik(uzytkownik_id):
    result = get_result_from_query(QUERY_SELECT_PRZODOWNIK_BY_UZYTKOWNIK_ID, [uzytkownik_id])
    if len(result) > 0:
        return True
    else:
        return False


def is_valid_for_adding_path(json_dict):
    odcinki = get_result_from_query(QUERY_SELECT_ODCINEK_BY_NAZWY_PUNKTOW,
                                    [json_dict['punkt_startowy'], json_dict['punkt_koncowy'],
                                     json_dict['punkt_startowy'], json_dict['punkt_koncowy']])

    if len(odcinki) > 0:
        return False

    return True


def insert_in_odcinek(json_dict):
    punkt_startowy = get_result_from_query(QUERY_SELECT_PUNKT_BY_NAZWA, [json_dict['punkt_startowy']])
    punkt_koncowy = get_result_from_query(QUERY_SELECT_PUNKT_BY_NAZWA, [json_dict['punkt_koncowy']])

    punkt_startowy_id = punkt_startowy[0][0]
    punkt_koncowy_id = punkt_koncowy[0][0]
    punktacja = int(json_dict['punktacja'])
    punktacja_odwrotnie = int(json_dict['punktacja_odwrotnie'])

    execute(QUERY_INSERT_INTO_ODCINEK,
            [punkt_startowy_id, punkt_koncowy_id, punktacja, punktacja_odwrotnie])

    return None


def get_trasy_do_potwierdzenia(user_id):
    return get_result_from_query(QUERY_SELECT_TRASY_DO_POTWIERDZENIA_BY_PRZODOWNIK_ID, [])


def get_pojedyncza_trasa_do_potwierdzenia(trasa_id):
    return get_result_from_query(QUERY_SELECT_POJEDYNCZA_TRASA_DO_POTWIERDZENIA_BY_TRASA_ID, [trasa_id])


def get_trasa_info(trasa_id):
    return get_result_from_query(QUERY_SELECT_TRASA_INFO_BY_TRASA_ID, [trasa_id])


def confirm_single_trasa(trasa_id, user_id):
    execute(QUERY_UPDATE_TRASA, [user_id, trasa_id])


def reject_single_trasa(trasa_id):
    execute(QUERY_DELETE_TRASA, [trasa_id])