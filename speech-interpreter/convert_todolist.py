from openai import OpenAI
import os
import json
from pathlib import Path

CURR_DIRECTORY = Path(__file__).parent

INSTRUCTION = """
    Hello, I know that you're are good at making todolist. For the given conversation, can you parse out the todo-items and the assignees.
    For example:
    Person 1 Name:
    - Task 1
    - Task 2
    Person 2 Name:
    - Task 1
    - Task 2
"""

class convertToDoList:
    def __init__(self, configPath = f'{CURR_DIRECTORY}/../config.json') -> None:
        self.config_path = os.path.expanduser(configPath)

        # Load the configuration file
        with open(self.config_path, 'r') as file:
            config = json.load(file)

        self.client = OpenAI(api_key=config['GPT_API_KEY'])

    def generate_todo_list(self, conversation):
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a helpful assistant that generates to-do lists from conversations."},
                {"role": "user", "content": f"Generate a to-do list from this conversation:\n{conversation}"}
            ],
            max_tokens=200,
            temperature=0.7
        )
        todo_list = response.choices[0].message.content.strip()
        return todo_list

    def callGPT(self, conversation):
        # Generate and print the to-do list
        todo_list = self.generate_todo_list(INSTRUCTION + "\n" + conversation)
        print(todo_list)

def mainCall(conversation):
    model = convertToDoList()
    model.callGPT(conversation)

if __name__ == "__main__":
    conversation = """
        Prince, can you do grocery shopping, buy a screen protector, and attend the parent meeting today? 
        Harry can pick up the kids and withdraw some money.
    """

    mainCall(conversation)