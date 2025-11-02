import os
import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from dotenv import load_dotenv


def setup_database_connection(env_path: str = '../local/.env') -> Engine:
    load_dotenv(env_path)

    db_url = os.getenv('DB_URL')
    db_username = os.getenv('DB_USERNAME')
    db_password = os.getenv('DB_PASSWORD')
    db_name = os.getenv('DB_NAME', 'cheaterbuster')
    db_port = os.getenv('DB_PORT', '3306')

    if not all([db_url, db_username, db_password]):
        raise ValueError(
            "Missing database credentials! "
            "Please set DB_URL, DB_USERNAME, and DB_PASSWORD in local/.env"
        )

    connection_string = f'mysql+pymysql://{db_username}:{db_password}@{db_url}:{db_port}/{db_name}'

    print(f"Connecting to database...")
    engine = create_engine(connection_string)
    print("Connection successful!")

    return engine

def load_all_players(engine: Engine) -> pd.DataFrame:
    df = pd.read_sql("SELECT * FROM player_data", engine)
    print(f"Loaded {len(df)} players")
    return df
