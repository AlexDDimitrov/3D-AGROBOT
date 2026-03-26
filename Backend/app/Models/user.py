import uuid
from ..db import get_connection

class UserModel:

    @staticmethod
    def create(first_name: str, last_name: str, email: str, hashed_password: str):
        conn = get_connection()
        cursor = conn.cursor()

        user_id = uuid.uuid4().bytes

        cursor.execute(
            "INSERT INTO users (id_bin, first_name, last_name, email, password) VALUES (%s, %s, %s, %s, %s)",
            (user_id, first_name, last_name, email, hashed_password)
        )
        conn.commit()
        cursor.close()
        conn.close()

    @staticmethod
    def find_by_email(email: str):
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        cursor.execute(
            "SELECT id_text, first_name, last_name, email, password FROM users WHERE email = %s",
            (email,)
        )
        user = cursor.fetchone()
        cursor.close()
        conn.close()
        return user