import flask
from flask import abort, request, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_caching import Cache
import database.database_caller as caller
import application.validator as validator

app = flask.Flask(__name__)
app.config["DEBUG"] = True
app.config['JSON_SORT_KEYS'] = False
app.config['JSON_AS_ASCII'] = False  # ensures UTF-8
limiter = Limiter(
    app,
    key_func=get_remote_address,
    default_limits=["5 per second"]
)

app.config["CACHE_TYPE"] = "simple"
app.config["CACHE_DEFAULT_TIMEOUT"] = 300

cache = Cache(app)


@app.route('/api/groups', methods=['GET'])
@cache.memoize(timeout=300)
def api_all_groups():
    result = caller.get_result_from_query(caller.QUERY_SELECT_ALL_GROUPS, [])

    return jsonify({'result': turn_array_of_groups_in_dictionary(result)})


@app.route('/register', methods=['POST'])
def register():
    json_dict = request.get_json()
    if validator.is_register_json_valid(json_dict):
        json_dict['data_urodzenia'] = validator.get_string_date_from_date(json_dict['data_urodzenia'])

        if caller.is_valid_for_register(json_dict):
            uzytkownik_id = caller.insert_in_uzytkownik(json_dict)
            return jsonify({"id": uzytkownik_id, "description": "OK"})
        else:
            return jsonify({"id": -1, "description": "login taken"})
    else:
        return jsonify({"id": -1, "description": "invalid data"})


@app.route('/login', methods=['POST'])
def login():
    json_dict = request.get_json()
    if validator.is_login_json_valid(json_dict):
        if json_dict['mail'] == 'admin' and json_dict['haslo'] == 'admin':
            return jsonify({"id": -100, "status": "Admin"})

        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                return jsonify({"id": user_id, "status": "Przodownik"})
            else:
                return jsonify({"id": user_id, "status": "Turysta"})
        else:
            return jsonify({"id": -1, "status": "Brak"})
    else:
        return jsonify({"id": -1, "status": "Brak"})


@app.route('/tours_to_confirm', methods=['POST'])
def tours_to_confirm():
    json_dict = request.get_json()
    if validator.is_login_json_valid(json_dict):
        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                trasy_dla_przewodnika = caller.get_trasy_do_potwierdzenia(user_id)
                if len(trasy_dla_przewodnika) > 0:
                    return jsonify({"id": user_id, "status": "OK",
                                    "result": turn_array_of_trasy_into_dictionary(trasy_dla_przewodnika)})
                else:
                    return jsonify({"id": user_id, "status": "Brak tras"})

            else:
                return jsonify({"id": user_id, "status": "Brak uprawnien"})
        else:
            return jsonify({"id": -1, "status": "Error"})
    else:
        return jsonify({"id": -1, "status": "Error"})


@app.route('/single_tour_to_confirm', methods=['POST'])
def single_tour_to_confirm():
    json_dict = request.get_json()
    if validator.is_single_tour_to_confirm(json_dict):
        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                trasa_do_potwierdzenia = caller.get_pojedyncza_trasa_do_potwierdzenia(json_dict['trasa_id'])
                trasa_do_potwierdzenia_info = caller.get_trasa_info(json_dict['trasa_id'])

                if len(trasa_do_potwierdzenia) > 0:
                    return jsonify(
                        {"id": user_id, "status": "OK", "data_rozpoczecia": trasa_do_potwierdzenia_info[0][0],
                         "data_zakonczenia": trasa_do_potwierdzenia_info[0][0],
                         "result": turn_array_of_single_tour_into_dictionary(trasa_do_potwierdzenia)})
                else:
                    return jsonify({"id": user_id, "status": "Brak tras"})

            else:
                return jsonify({"id": user_id, "status": "Brak uprawnien"})
        else:
            return jsonify({"id": -1, "status": "Error"})
    else:
        return jsonify({"id": -1, "status": "Error"})


@app.route('/confirm_single_tour', methods=['POST'])
def confirm_single_tour():
    json_dict = request.get_json()
    if validator.is_single_tour_to_confirm(json_dict):
        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                caller.confirm_single_trasa(json_dict['trasa_id'], user_id)

                return jsonify({"status": "OK"})

            else:
                return jsonify({"status": "Brak uprawnien"})
        else:
            return jsonify({"status": "Error"})
    else:
        return jsonify({"status": "Error"})


@app.route('/reject_single_tour', methods=['POST'])
def reject_single_tour():
    json_dict = request.get_json()
    if validator.is_single_tour_to_confirm(json_dict):
        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                caller.reject_single_trasa(json_dict['trasa_id'])

                return jsonify({"status": "OK"})

            else:
                return jsonify({"status": "Brak uprawnien"})
        else:
            return jsonify({"status": "Error"})
    else:
        return jsonify({"status": "Error"})


@app.route('/tour_to_confirm', methods=['POST'])
def trasy_do_potwierdzenia():
    json_dict = request.get_json()
    if validator.is_login_json_valid(json_dict):
        user_id = caller.log_in(json_dict)

        if user_id > 0:
            if caller.is_przodownik(user_id):
                trasy_dla_przewodnika = caller.get_trasy_do_potwierdzenia(user_id)
                if len(trasy_dla_przewodnika) > 0:
                    return jsonify({"id": user_id, "status": "OK",
                                    "result": turn_array_of_trasy_into_dictionary(trasy_dla_przewodnika)})
                else:
                    return jsonify({"id": user_id, "status": "Brak tras"})

            else:
                return jsonify({"id": user_id, "status": "Brak uprawnien"})
        else:
            return jsonify({"id": -1, "status": "Error"})
    else:
        return jsonify({"id": -1, "status": "Error"})


@app.route('/add_path', methods=['POST'])
def new_path():
    json_dict = request.get_json()
    if validator.is_add_path_json_valid(json_dict):
        if json_dict['mail'] == 'admin' and json_dict['haslo'] == 'admin':
            del json_dict['mail']
            del json_dict['haslo']

            if caller.is_valid_for_adding_path(json_dict):
                caller.insert_in_odcinek(json_dict)
                return jsonify({"status": "ADDED"})

    return jsonify({"status": "ERROR"})


@app.route('/api/group/<group_id>', methods=['GET'])
@cache.memoize(timeout=300)
def api_group_info(group_id):
    if group_id.isnumeric():
        group_points = caller.get_points_from_group(group_id)

        if len(group_points) == 0:
            abort(400, "No points in group of this id")

        group_paths = caller.get_paths_from_group(group_id)

        return jsonify(
            {'points': turn_points_in_dictionary(group_points), 'paths': turn_paths_in_dictionary(group_paths)})

    else:
        abort(400, "Wrong group_id format")


def turn_array_of_groups_in_dictionary(groups_array):
    results = []
    for row in groups_array:
        results.append({'id': row[0], 'nazwa': row[1]})

    return results


def turn_array_of_trasy_into_dictionary(trasy_dla_przewodnika):
    results = []
    for row in trasy_dla_przewodnika:
        results.append({'uzytkownik_id': row[0], 'nazwa': row[1] + ' ' + row[2], 'trasa_id': row[3]})

    return results


def turn_paths_in_dictionary(group_paths):
    results = []
    for row in group_paths:
        results.append(
            {'id': row[0], 'punktacja': row[1], 'punktacja_odwrotnie': row[2], 'punkt_startowy': row[3],
             'punkt_koncowy': row[4]})

    return results


def turn_array_of_single_tour_into_dictionary(trasa_do_potwierdzenia):
    results = []
    for row in trasa_do_potwierdzenia:
        results.append(
            {'punkt_startowy': row[0], 'punkt_koncowy': row[1], 'punktacja': row[2], 'liczba_porzadkowa': row[3]})

    return results


def turn_points_in_dictionary(group_points):
    results = []
    for row in group_points:
        results.append(
            {'id': row[0], 'nazwa': row[1], 'grupa_gorska_id': row[2]})

    return results


if __name__ == "__main__":
    app.run()
