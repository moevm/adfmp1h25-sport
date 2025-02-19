from datetime import timedelta
from flask import Flask
from flask_jwt_extended import JWTManager
from endpoints.auth.auth import auth
from endpoints.predict.predict import predict
from endpoints.teams.teams import teams
from services.get_env import PORT, JWT_KEY

app = Flask(__name__)
app.config['JWT_SECRET_KEY'] = JWT_KEY
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=30)
app.config['JWT_REFRESH_TOKEN_EXPIRES'] = timedelta(days=30)
app.config["JWT_HEADER_NAME"] = "Authorization"
app.config["JWT_HEADER_TYPE"] = "Bearer"
app.config["JWT_COOKIE_CSRF_PROTECT"] = False

jwt_manager = JWTManager(app)

app.register_blueprint(auth, url_prefix='/auth')
app.register_blueprint(teams, url_prefix='/teams')
app.register_blueprint(predict, url_prefix='/predict')


@app.route('/is_ok')
def home():
    return "Server is running"


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=PORT, debug=True)
