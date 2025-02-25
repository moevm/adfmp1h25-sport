from dataclasses import dataclass


@dataclass
class UserStats:
    predicted_games: int
    winner_points: int
    score_points: int
    following_count: int
    followers_count: int

    @staticmethod
    def from_json(data: dict) -> 'UserStats':
        return UserStats(
            predicted_games=data.get('predicted_games', 0),
            winner_points=data.get('winner_points', 0),
            score_points=data.get('score_points', 0),
            following_count=data.get('following_count', 0),
            followers_count=data.get('followers_count', 0)
        )

    def to_json(self) -> dict:
        return {
            'predicted_games': self.predicted_games,
            'winner_points': self.winner_points,
            'score_points': self.score_points,
            'following_count': self.following_count,
            'followers_count': self.followers_count
        }
