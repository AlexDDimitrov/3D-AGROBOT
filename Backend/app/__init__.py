from flask import Flask
from dotenv import load_dotenv
from .config import Config

load_dotenv()

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    from .routes.auth import auth_bp
    app.register_blueprint(auth_bp, url_prefix="/auth")

    return app