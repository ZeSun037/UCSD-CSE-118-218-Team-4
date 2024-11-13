import subprocess
import os
import json
from pathlib import Path

CURR_DIRECTORY = Path(__file__).parent

class textSpeech:
    def __init__(self, model_path = f'{CURR_DIRECTORY}/deepspeech/deepspeech-0.9.3-models.pbmm', scorer_path = f'{CURR_DIRECTORY}/deepspeech/deepspeech-0.9.3-models.scorer', audio_path = f'{CURR_DIRECTORY}/input-data/output.wav'):
        self.model_path = model_path
        self.scorer_path = scorer_path
        self.audio_path = audio_path

    def convertTextSpeech(self):
        # Command to run DeepSpeech with your model, scorer, and audio file
        command = [
            "deepspeech",
            "--model", self.model_path,
            "--scorer", self.scorer_path,
            "--audio", self.audio_path
        ]

        # Run the command and capture the output
        result = subprocess.run(command, capture_output=True, text=True)

        # Store the output in a variable
        transcription = result.stdout.strip()

        # Print the transcription
        # print("Transcription:", transcription)

        return transcription

def mainCall(audio_path = f'{CURR_DIRECTORY}/input-data/output.wav'):
    model = textSpeech(audio_path=audio_path)
    return model.convertTextSpeech()
    
if __name__ == "__main__":
    mainCall()