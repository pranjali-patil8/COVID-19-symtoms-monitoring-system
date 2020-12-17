from __future__ import print_function

import datetime as DT
import sqlite3
import time
from datetime import datetime

from flask import Flask, request, g
from flask import render_template
from haversine import haversine

from tabulate import tabulate

app = Flask(__name__)

main_visited = set()
sub_visited = set()

adjacency_matrix = [[0] * 12 for i in range(12)]
user_Id = 0
date = 0
q= []
def connect_to_database():
    return sqlite3.connect(app.config['DATABASE'])

def get_db():
    db = getattr(g, 'db', None)
    db = g.db = connect_to_database()
    return db


@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, 'db', None)
    if db is not None:
        db.close()


def execute_query(query, args=()):
    cur = get_db().execute(query, args)
    rows = cur.fetchall()
    cur.close()
    return rows


@app.route('/', methods=['GET', 'POST'], endpoint='index')
def index():
    return render_template('index.html')


@app.route('/android_data', methods=['GET', 'POST'], endpoint='android_data')
def handle_data():
    global user_Id
    global date
    global q
    if request.method == "POST":
        response = dict(request.form)
        print(response)
        data_from_android = request.files['upload_file']
        if data_from_android.filename != '':
            data_from_android.save(data_from_android.filename)
        user_Id = response['user_Id']
        date = response['date']
        print(user_Id, date)
        q= [[user_Id, date]]

    adjacency_matrix = viewdb()
    return str(adjacency_matrix)


def data_lat_long(user_Id):
    DATABASE = "./Database_files/LifeMap_GS" + str(user_Id) + ".db"
    app.config['DATABASE'] = DATABASE
    temp_date = str(date) + "%"
    rows = execute_query(
        """SELECT _latitude, _longitude FROM locationTable
        WHERE _time_location LIKE ? """, (temp_date,)
    )
    return (rows)


def viewdb():
    while q:
        user_Id, date = q.pop(0)

        if user_Id in main_visited:
            continue
        main_visited.add(user_Id)

        sub_visited = set()
        sub_visited.add(user_Id)
        total_lat_long = data_lat_long(user_Id)
        for l in total_lat_long:
            for i in range(1, 13):
                if i == int(user_Id):
                    continue
                else:
                    DATABASE = "./Database_files/LifeMap_GS" + str(i) + ".db"
                    app.config['DATABASE'] = DATABASE

                    dates = []
                    date_convert = datetime.strptime(date, '%Y%m%d')
                    date_week = [date_convert - DT.timedelta(days=x) for x in range(7)]

                    for d in range(len(date_week)):
                        dates.append(str(date_week[d].strftime('%Y%m%d')) + "%")

                    rows = execute_query(
                        """SELECT _latitude, _longitude, _time_location FROM locationTable 
                        WHERE _time_location LIKE ? 
                        OR _time_location LIKE ?
                        OR _time_location LIKE ?
                        OR _time_location LIKE ?
                        OR _time_location LIKE ?
                        OR _time_location LIKE ?
                        OR _time_location LIKE ?""", (dates[0], dates[1], dates[2],
                                                      dates[3], dates[4], dates[5], dates[6]))
                    for t in rows:
                        distance = haversine((l[0] / 10 ** 6, l[1] / 10 ** 6), (t[0] / 10 ** 6, t[1] / 10 ** 6))
                        if distance < 5.0 and str(i) not in sub_visited:
                            q.append([i, t[2][:8]])
                            adjacency_matrix[int(user_Id) - 1][i - 1] = 1
                            adjacency_matrix[i - 1][int(user_Id) - 1] = 1
                            sub_visited.add(str(i))

                            break
                        else:
                            continue
    return adjacency_matrix


@app.route("/adjacency", endpoint="adjacency")
def computeAdjMatrix():
    time.sleep(20)
    return render_template('adjacency.html')


@app.route("/result", endpoint="result")
def msg_success():
    return render_template('result.html')


@app.route("/finalresult", endpoint="finalresult")
def displayAdjMatrix():
    return tabulate(adjacency_matrix, tablefmt="html")


if __name__ == "__main__":
    app.jinja_env.auto_reload = True
    app.config['TEMPLATES_AUTO_RELOAD'] = True
    app.run(debug=True)

































