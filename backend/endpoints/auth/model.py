from dataclasses import dataclass
from typing import Optional


@dataclass
class AuthData:
    login: str
    password: str
    avatar: Optional[str] = None

    @staticmethod
    def from_json(data: dict) -> 'AuthData':
        if not isinstance(data, dict):
            raise ValueError("Input must be dictionary")
        return AuthData(
            login=data.get('login'),
            password=data.get('password'),
        )

    def to_json(self) -> dict:
        return {
            'login': self.login,
            'password': self.password,
        }

    def is_valid(self) -> bool:
        return bool(self.login and self.validate_password_strength())

    def validate_password_strength(self) -> bool:
        return len(self.password) >= 8
