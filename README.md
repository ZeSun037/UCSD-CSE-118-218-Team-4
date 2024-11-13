## Note
- Before running any python script in **[speech-interpreter](https://github.com/ZeSun037/UCSD-CSE-118-218-Team-4/tree/speech-interpreter/speech-interpreter)** directory, download [deepspeech-0.9.3-models.pbmm](https://github.com/mozilla/DeepSpeech/releases/download/v0.9.3/deepspeech-0.9.3-models.pbmm) and [deepspeech-0.9.3-models.scorer](https://github.com/mozilla/DeepSpeech/releases/download/v0.9.3/deepspeech-0.9.3-models.scorer) and store these models inside the **[deepspeech](https://github.com/ZeSun037/UCSD-CSE-118-218-Team-4/tree/speech-interpreter/speech-interpreter/deepspeech)** directory.

## Directory Structure Map
    speech-interpreter/
        ├── deepspeech/
            ├── deepspeech-0.9.3-models.pbmm        # TO DOWNLOAD
            └── deepspeech-0.9.3-models.scorer      # TO DOWNLOAD
        ├── input-data/
            └── testing.wav
        └── subdirectory/
            ├── requirements.txt
            ├── convert_todolist.py
            ├── speech_list.py                      # Run this
            └── text_speech.py