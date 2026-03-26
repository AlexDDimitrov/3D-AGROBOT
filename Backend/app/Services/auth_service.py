import bcrypt
from ..models.user import UserModel

def register_user(first_name: str, last_name: str, email: str, password: str):
    existing = UserModel.find_by_email(email)
    if existing:
        return None, "Имейлът вече се използва"

    hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()
    UserModel.create(first_name, last_name, email, hashed)
    return True, "Акаунтът е създаден успешно"