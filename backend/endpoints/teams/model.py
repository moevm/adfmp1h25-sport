from dataclasses import dataclass
from typing import Optional


@dataclass
class EventData:
    id: int
    start_at: int
    start_at_day: int
    team_a_id: int
    team_b_id: int
    period: Optional[str] = None
    score: Optional[str] = None

    @staticmethod
    def from_json(data: dict) -> 'EventData':
        event = data.get('event', {})
        return EventData(
            id=event.get('id'),
            start_at=event.get('start_at'),
            start_at_day=event.get('start_at_day'),
            team_a_id=event.get('team_a', {}).get('id'),
            team_b_id=event.get('team_b', {}).get('id'),
            period=event.get('period'),
            score=event.get('score')
        )

    def to_json(self) -> dict:
        return {
            'event': {
                'id': self.id,
                'start_at': self.start_at,
                'start_at_day': self.start_at_day,
                'team_a': {'id': self.team_a_id},
                'team_b': {'id': self.team_b_id},
                'period': self.period,
                'score': self.score
            }
        }