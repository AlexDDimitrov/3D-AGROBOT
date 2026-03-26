from ..db import get_connection

class GardenModel:

    @staticmethod
    def get_by_user_id(user_id: int):
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        cursor.execute(
            "SELECT id, garden_name, garden_width, garden_height, path_width, number_beds FROM gardens WHERE user_id = %s",
            (user_id,)
        )
        gardens = cursor.fetchall()
        cursor.close()
        conn.close()
        return gardens