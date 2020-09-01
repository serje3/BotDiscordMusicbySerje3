# import sqlite3
import psycopg2
from psycopg2 import OperationalError
from settings import DATABASE_URL
from urllib.parse import urlparse


class ManageDB:
    def __init__(self):
        self.conn = self.create_connection()
        cursor = self.conn.cursor()
        cursor.execute("""CREATE TABLE IF NOT EXISTS songs(
                           id INTEGER PRIMARY KEY NOT NULL ,
                           song text NOT NULL,
                           guild INTEGER NOT NULL
        );""")
        self.conn.commit()
        cursor.close()

    def create_connection(self):
        parsed_url = urlparse(DATABASE_URL)
        db_name = parsed_url.path[1:]
        db_user = parsed_url.username
        db_password = parsed_url.password
        db_host = parsed_url.hostname
        db_port = parsed_url.port
        connection = None
        try:
            connection = psycopg2.connect(
                database=db_name,
                user=db_user,
                password=db_password,
                host=db_host,
                port=db_port
            )
            print('База данных подключена')
        except OperationalError as e:
            print(f"Ошибка, все хуйня: {e}")
        return connection

    def insert(self, arr):
        cursor = self.conn.cursor()
        cursor.executemany("""INSERT INTO songs(song,guild) VALUES(%s,%s)""", arr)
        self.conn.commit()
        cursor.close()

    def select(self, guild_id):
        cursor = self.conn.cursor()
        cursor.execute("""SELECT * FROM songs WHERE guild=%(guild_id)s""", {'guild_id': guild_id})
        result = cursor.fetchall()
        cursor.close()
        return result

    def drop(self, id_arr, guild):
        cursor = self.conn.cursor()
        for _id in id_arr:
            cursor.execute("""DELETE FROM songs WHERE id= %(id)s and guild=%(guild_id)s""", {'id': _id, 'guild_id': guild})
            self.conn.commit()
        cursor.close()

    def close_connection(self):
        self.conn.close()
