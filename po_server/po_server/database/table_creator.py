import os
import sqlite3
import re
import sys

QUERY_DROP_ALL_TABLES = """
DROP TABLE IF EXISTS GrupaGorska;
DROP TABLE IF EXISTS Punkt;
DROP TABLE IF EXISTS Odcinek;
DROP TABLE IF EXISTS CzescTrasy;
DROP TABLE IF EXISTS Odznaka;
DROP TABLE IF EXISTS OdznakaTurysty;
DROP TABLE IF EXISTS Przodownik;
DROP TABLE IF EXISTS Trasa;
DROP TABLE IF EXISTS TrasyDoOdznaki;
DROP TABLE IF EXISTS Turysta;
DROP TABLE IF EXISTS UczestnicyWycieczki;
DROP TABLE IF EXISTS Uprawnienia;
DROP TABLE IF EXISTS Uzytkownik;
"""

QUERY_CREATE_TABLE_GRUPA_GORSKA = """
CREATE TABLE GrupaGorska (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
nazwa varchar(255) NOT NULL UNIQUE);
"""

QUERY_CREATE_TABLE_PUNKT = """
CREATE TABLE Punkt (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
nazwa varchar(255) NOT NULL,
grupa_gorska_id integer(10) NOT NULL,
FOREIGN KEY(grupa_gorska_id) REFERENCES GrupaGorska(id));
"""

QUERY_CREATE_TABLE_ODCINEK = """
CREATE TABLE Odcinek (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
punktacja integer(10) NOT NULL,
punktacja_odwrotnie integer(10) NOT NULL,
punkt_startowy integer(10) NOT NULL,
punkt_koncowy integer(10) NOT NULL,
FOREIGN KEY(punkt_startowy) REFERENCES Punkt(id),
FOREIGN KEY(punkt_koncowy) REFERENCES Punkt(id),
CONSTRAINT punktacja_nieujemna
CHECK (punktacja >= 0),
CONSTRAINT punktacja_odwrotnie_nieujemna
CHECK (punktacja_odwrotnie >= 0));
"""

QUERY_CREATE_TABLE_CZESC_TRASY = """
CREATE TABLE CzescTrasy (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
czy_odwrotnie integer(1) NOT NULL,
liczba_porzadkowa integer(10) NOT NULL,
odcinek_id integer(10) NOT NULL,
trasa_id integer(10) NOT NULL,
FOREIGN KEY(odcinek_id) REFERENCES Odcinek(id),
FOREIGN KEY(trasa_id) REFERENCES Trasa(id),
CONSTRAINT liczba_porzadkowa_nieujemna
CHECK (liczba_porzadkowa >= 0));
"""

QUERY_CREATE_TABLE_ODZNAKA = """
CREATE TABLE Odznaka (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
nazwa varchar(30) NOT NULL,
stopien varchar(30) NOT NULL,
wymagane_punkty integer(10) NOT NULL);
"""

QUERY_CREATE_TABLE_ODZNAKA_TURYSTY = """
CREATE TABLE OdznakaTurysty (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
data_zdobycia date,
uzytkownik_id integer(10) NOT NULL,
odznaka_id integer(10) NOT NULL,
przodownik_zatwierdzajacy_id integer(10),
FOREIGN KEY(uzytkownik_id) REFERENCES Uzytkownik(id),
FOREIGN KEY(odznaka_id) REFERENCES Odznaka(id),
FOREIGN KEY(przodownik_zatwierdzajacy_id) REFERENCES Przodownik(uzytkownik_id));
"""

QUERY_CREATE_TABLE_PRZODOWNIK = """
CREATE TABLE Przodownik (
uzytkownik_id integer(10) NOT NULL,
nr_legitymacji integer(10) NOT NULL UNIQUE,
PRIMARY KEY (uzytkownik_id),
FOREIGN KEY(uzytkownik_id) REFERENCES Uzytkownik(id));
"""

QUERY_CREATE_TABLE_TRASA = """
CREATE TABLE Trasa (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
data_rozpoczecia date NOT NULL,
data_zakonczenia date NOT NULL,
potwierdzona_przez integer(10),
uzytkownik_id integer(10) NOT NULL,
FOREIGN KEY(potwierdzona_przez) REFERENCES Przodownik(uzytkownik_id),
FOREIGN KEY(uzytkownik_id) REFERENCES Uzytkownik(id),
CONSTRAINT kolejnosc_dat
CHECK (data_rozpoczecia <= data_zakonczenia));
"""

QUERY_CREATE_TABLE_TRASY_DO_ODZNAKI = """
CREATE TABLE TrasyDoOdznaki (
odznaka_turysty_id integer(10) NOT NULL,
trasa_id integer(10) NOT NULL,
PRIMARY KEY (odznaka_turysty_id,
trasa_id),
FOREIGN KEY(odznaka_turysty_id) REFERENCES OdznakaTurysty(id),
FOREIGN KEY(trasa_id) REFERENCES Trasa(id));
"""

QUERY_CREATE_TABLE_TURYSTA = """
CREATE TABLE Turysta (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
nadmiarowe_punkty integer(10) NOT NULL,
uzytkownik_id integer(10) NOT NULL,
FOREIGN KEY(uzytkownik_id) REFERENCES Uzytkownik(id));
"""

QUERY_CREATE_TABLE_UCZESTNICY_WYCIECZKI = """
CREATE TABLE UczestnicyWycieczki (
uzytkownik_id integer(10) NOT NULL,
trasa_id integer(10) NOT NULL,
PRIMARY KEY (uzytkownik_id,
trasa_id),
FOREIGN KEY(uzytkownik_id) REFERENCES Uzytkownik(id),
FOREIGN KEY(trasa_id) REFERENCES Trasa(id));
"""

QUERY_CREATE_TABLE_UPRAWNIENIA = """
CREATE TABLE Uprawnienia (
grupa_gorska_id integer(10) NOT NULL,
przodownik_uzytkownik_id integer(10) NOT NULL,
PRIMARY KEY (grupa_gorska_id,
przodownik_uzytkownik_id),
FOREIGN KEY(grupa_gorska_id) REFERENCES GrupaGorska(id),
FOREIGN KEY(przodownik_uzytkownik_id) REFERENCES Przodownik(uzytkownik_id));
"""

QUERY_CREATE_TABLE_UZYTKOWNIK = """
CREATE TABLE Uzytkownik (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
mail varchar(255) NOT NULL UNIQUE,
haslo varchar(30) NOT NULL,
imie varchar(255) NOT NULL,
nazwisko varchar(255) NOT NULL,
data_urodzenia date NOT NULL);
"""

QUERY_INSERT_PODTATRZE = """
INSERT INTO GrupaGorska (nazwa) VALUES ('Podtatrze');
"""

QUERY_INSERT_INTO_PUNKT = """
INSERT INTO Punkt (nazwa, grupa_gorska_id) VALUES (?, ?);
"""

QUERY_INSERT_INTO_ODZNAKA = """
INSERT INTO Odznaka (nazwa, stopien, wymagane_punkty) VALUES (?, ?, ?);
"""

QUERY_INSERT_INTO_UZYTKOWNIK = """
INSERT INTO Uzytkownik (mail, haslo, imie, nazwisko, data_urodzenia) VALUES (?, ?, ?, ?, ?);
"""

QUERY_INSERT_INTO_PRZODOWNIK = """
INSERT INTO Przodownik (uzytkownik_id, nr_legitymacji) VALUES ((SELECT MAX(id) FROM Uzytkownik WHERE mail = ?), ?);
"""

QUERY_INSERT_INTO_UPRAWNIENIA = """
INSERT INTO Uprawnienia (przodownik_uzytkownik_id, grupa_gorska_id) 
VALUES ((SELECT MAX(id) FROM Uzytkownik WHERE mail = ?), (SELECT MAX(id) FROM GrupaGorska WHERE nazwa = ?));
"""

QUERY_SELECT_PODTATRZE_ID = """
SELECT id FROM GrupaGorska WHERE nazwa = 'Podtatrze';
"""

QUERY_SELECT_PUNKTY_Z_PODTATRZE = """
SELECT id, nazwa FROM Punkt WHERE grupa_gorska_id = (SELECT MAX(id) FROM GrupaGorska WHERE nazwa = 'Podtatrze');
"""

QUERY_INSERT_INTO_ODCINEK = """
INSERT INTO Odcinek (punkt_startowy, punkt_koncowy, punktacja, punktacja_odwrotnie) 
VALUES (?, ?, ?, ?);
"""

QUERY_INSERT_INTO_TRASA = """
INSERT INTO Trasa (data_rozpoczecia,data_zakonczenia,potwierdzona_przez,uzytkownik_id) 
VALUES (?, ?, NULL, (SELECT MAX(id) FROM Uzytkownik WHERE mail = ?));
"""

QUERY_INSERT_INTO_CZESC_TRASY = """
INSERT INTO CzescTrasy (czy_odwrotnie,liczba_porzadkowa,odcinek_id,trasa_id) 
VALUES (?, ?, ?, ?);
"""

podtatrze_csv = os.path.join(sys.path[0], 'podtatrze.csv')
got_sqlite = '../GOT.sqlite'  # os.path.join(sys.path[0], 'GOT.sqlite')


def create_tables():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    for single_table_drop in QUERY_DROP_ALL_TABLES.splitlines():
        cursor.execute(single_table_drop)

    cursor.execute(QUERY_CREATE_TABLE_GRUPA_GORSKA)
    cursor.execute(QUERY_CREATE_TABLE_PUNKT)
    cursor.execute(QUERY_CREATE_TABLE_ODCINEK)
    cursor.execute(QUERY_CREATE_TABLE_CZESC_TRASY)
    cursor.execute(QUERY_CREATE_TABLE_ODZNAKA)
    cursor.execute(QUERY_CREATE_TABLE_ODZNAKA_TURYSTY)
    cursor.execute(QUERY_CREATE_TABLE_PRZODOWNIK)
    cursor.execute(QUERY_CREATE_TABLE_TRASA)
    cursor.execute(QUERY_CREATE_TABLE_TRASY_DO_ODZNAKI)
    cursor.execute(QUERY_CREATE_TABLE_TURYSTA)
    cursor.execute(QUERY_CREATE_TABLE_UCZESTNICY_WYCIECZKI)
    cursor.execute(QUERY_CREATE_TABLE_UPRAWNIENIA)
    cursor.execute(QUERY_CREATE_TABLE_UZYTKOWNIK)

    connection.commit()

    cursor.close()
    connection.close()


def insert_paths():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    cursor.execute(QUERY_SELECT_PUNKTY_Z_PODTATRZE)
    points = cursor.fetchall()

    points_dictionary = {}

    for row in points:
        points_dictionary[row[1]] = row[0]

    file_podtatrze = open(podtatrze_csv, 'r', encoding='utf8')
    lines = file_podtatrze.readlines()
    file_podtatrze.close()

    pattern = r"([^,]+),([^,]+),(\d+),(\d+)"

    for row in lines:
        match = re.search(pattern, row)
        if match:
            cursor.execute(QUERY_INSERT_INTO_ODCINEK,
                           [points_dictionary[match.group(1)], points_dictionary[match.group(2)], match.group(3),
                            match.group(4)])

    connection.commit()

    cursor.close()
    connection.close()


def insert_badges():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    cursor.execute(QUERY_INSERT_INTO_ODZNAKA, ['Popularna', '', 60])
    cursor.execute(QUERY_INSERT_INTO_ODZNAKA, ['Mała', 'Brązowy', 120])
    cursor.execute(QUERY_INSERT_INTO_ODZNAKA, ['Mała', 'Srebrny', 360])
    cursor.execute(QUERY_INSERT_INTO_ODZNAKA, ['Mała', 'Złoty', 720])

    connection.commit()

    cursor.close()
    connection.close()


def insert_przodownik():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    cursor.execute(QUERY_INSERT_INTO_UZYTKOWNIK,
                   ['przodownik@gmail.com', 'przodownik', 'Jan', 'Kowalski', '1990-01-01'])
    connection.commit()
    cursor.execute(QUERY_INSERT_INTO_PRZODOWNIK, ['przodownik@gmail.com', '1234'])
    cursor.execute(QUERY_INSERT_INTO_UPRAWNIENIA, ['przodownik@gmail.com', 'Podtatrze'])

    connection.commit()

    cursor.close()
    connection.close()


def insert_przykladowa_trasa():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    cursor.execute(QUERY_INSERT_INTO_UZYTKOWNIK,
                   ['turysta@gmail.com', 'turysta', 'Adam', 'Adamski', '1991-01-01'])
    connection.commit()

    cursor.execute(QUERY_INSERT_INTO_TRASA,
                   ['2020-01-01', '2020-01-02', 'turysta@gmail.com'])
    connection.commit()

    cursor.execute(QUERY_INSERT_INTO_CZESC_TRASY, [0, 1, 6, 1])
    cursor.execute(QUERY_INSERT_INTO_CZESC_TRASY, [0, 2, 10, 1])
    connection.commit()

    cursor.close()
    connection.close()


def insert_data():
    setup_podtatrze()
    insert_paths()
    insert_badges()
    insert_przodownik()
    insert_przykladowa_trasa()


def setup_podtatrze():
    connection = sqlite3.connect(got_sqlite)
    cursor = connection.cursor()

    cursor.execute(QUERY_INSERT_PODTATRZE)
    cursor.execute(QUERY_SELECT_PODTATRZE_ID)
    group_id = cursor.fetchall()[0][0]

    file_podtatrze = open(podtatrze_csv, 'r', encoding='utf8')
    lines = file_podtatrze.readlines()
    file_podtatrze.close()

    pattern = r"([^,]+),([^,]+),\d+,\d+"
    list_of_all_points = []
    for row in lines:
        match = re.search(pattern, row)
        if match:
            if match.group(1) not in list_of_all_points:
                list_of_all_points.append(match.group(1))
            if match.group(2) not in list_of_all_points:
                list_of_all_points.append(match.group(2))

    for point in list_of_all_points:
        cursor.execute(QUERY_INSERT_INTO_PUNKT, [point, group_id])

    connection.commit()

    cursor.close()
    connection.close()


if __name__ == "__main__":
    create_tables()
    insert_data()
