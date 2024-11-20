import convert_todolist
import text_speech
from pathlib import Path

CURR_DIRECTORY = Path(__file__).parent


if __name__ == "__main__":
    # Calling Speech to Text conversion
    print("Running Speech to Text conversion \n")
    transcription = text_speech.mainCall(f'{CURR_DIRECTORY}/input-data/testing.wav')

    # Calling Text to ToDo-List conversion
    print("Running Text to ToDo-List conversion \n")
    convert_todolist.mainCall(transcription)

