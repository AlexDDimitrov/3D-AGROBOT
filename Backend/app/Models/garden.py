from ..db import get_connection

class GardenModel:

    @staticmethod
    def create(user_id: int, garden_name: str, garden_width: int, garden_height: int, path_width: int, number_beds: int, plant: str):
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute(
            """
            INSERT INTO gardens (user_id, garden_name, garden_width, garden_height, path_width, number_beds, plant)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """,
            (user_id, garden_name, garden_width, garden_height, path_width, number_beds, plant)
        )
        conn.commit()
        cursor.close()
        conn.close()

    @staticmethod
    @staticmethod
    def get_by_user_id(user_id: int):
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        cursor.execute(
            "SELECT id, garden_name, garden_width, garden_height, path_width, number_beds, plant FROM gardens WHERE user_id = %s",
            (user_id,)
        )
        gardens = cursor.fetchall()
        cursor.close()
        conn.close()
        return gardens