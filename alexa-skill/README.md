# Alexa Developer Console Build Directory

    Custom/
        ├── Invocations/
            └── Skill Invocation Name     # Change Name to 'my lab'
        ├── Interaction Model/
            └── Intents
                ├── AMAZON.Cancellntent
                ├── AMAZON.HelpIntent
                ├── AMAZON.StopIntentpy
                    └── Utterances: have a nice day, Thank you, I am good, done
                ├── HelloWorldIntent
                    ├── Utterances: hello, how are you, say hi 
                    world, say hi, hi
                    └── Intent Slots: Name - catchAll, Slot Type - AMAZON.SearchQuery
                        ├── Alexa speech prompts: To add a task, please indicate the task, assignee, and which to-do list.
                        └── User Utterances: {catchAll}
                ├── AMAZON.NavigateHomelntent
                ├── AMAZON.FallbackIntentStopIntentpy
                └── AddTaskIntent
                    ├── Utterances: sure, yes please, yes
                    └── Intent Slots: Name - catchAll, Slot Type - AMAZON.SearchQuery
                        ├── Alexa speech prompts: please add your task
                        └── User Utterances: {catchAll}