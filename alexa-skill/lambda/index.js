/* *
 * This sample demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK (v2).
 * Please visit https://alexa.design/cookbook for additional examples on implementing slots, dialog management,
 * session persistence, api calls, and more.
 * */
const Alexa = require('ask-sdk-core');
const axios = require('axios');

const LaunchRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'LaunchRequest';
    },
    handle(handlerInput) {
        const speakOutput = 'Hello';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

async function makeLocalServerRequest(catchAllValue) {
    try {
        console.log("Making a request to server")
        const response = await axios.post(
            'http://83.149.103.151:3000/echo/new',
            {
                messages: [
                    {
                        role: "system",
                        content: "You are a task organizer that formats requests into structured to-do lists. If a user mentions an assignee, a task, and the todo-list type, you should create a structured response like this:\n\n[Type] ToDo-List\nAssignee [Name]:\n1. [Task 1]\n2. [Task 2]\n\nRespond with only the formatted to-do list."
                    },
                    {
                        role: "user",
                        content: "test first" // catchAllValue
                    }
                ]
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );

        if (response.status === 200) {
            console.log('Response from Local Server:', response.data);
            return response.data;
        } else {
            console.error('Server request failed with status code:', response.status);
            return null;
        }
    } catch (error) {
        console.error("Error making the request to the local server:", error.message);
        return null;
    }
}

// Function to make the POST request to OpenAI API
async function makeAsyncPostRequest(catchAllValue) {
    try {
        const response = await axios.post(
            'https://api.openai.com/v1/chat/completions',
            {
                model: "gpt-4",
                messages: [
                    {
                        role: "system",
                        content: "You are a task organizer that formats requests into structured to-do lists. If a user mentions an assignee, a task, and the todo-list type, you should create a structured response like this:\n\n[Type] ToDo-List\nAssignee [Name]:\n1. [Task 1]\n2. [Task 2]\n\nRespond with only the formatted to-do list."
                    },
                    {
                        role: "user",
                        content: catchAllValue
                    }
                ]
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${process.env.OPENAI_API_KEY}`
                }
            }
        );

        if (response.status === 200) {
            const responseData = response.data.choices[0].message.content;
            const parsedData = parseToDoList(responseData);
            console.log('Parsed Data:', parsedData);
            return parsedData;
        } else {
            console.error('Failed with status code:', response.status);
            return null;
        }
    } catch (error) {
        console.error("Error making the request:", error.message);
        return null;
    }
}

// Function to parse the to-do list response
function parseToDoList(response) {
    const parsedData = {
        assignee: '',
        tasks: [],
        type: ''
    };

    const typeMatch = response.match(/(.*?) ToDo-List/);
    const assigneeMatch = response.match(/Assignee\s*(\w+):/);
    const taskMatches = response.match(/(\d+)\.\s*(.*)/g);

    if (typeMatch) {
        parsedData.type = typeMatch[1].trim();
    }
    if (assigneeMatch) {
        parsedData.assignee = assigneeMatch[1].trim();
    }
    if (taskMatches) {
        parsedData.tasks = taskMatches.map(task => task.replace(/^\d+\.\s*/, '').trim());
    } else {
        const singleTaskMatch = response.match(/\d+\.\s*(.*)/);
        if (singleTaskMatch) {
            parsedData.tasks = [singleTaskMatch[1].trim()];
        }
    }

    return parsedData;
}

const HelloWorldIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'HelloWorldIntent';
    },
    async handle(handlerInput) {
        let speakOutput = 'Hello World!';
        const catchAllValue = handlerInput.requestEnvelope.request.intent.slots.catchAll.value;
        console.log("User Input:", catchAllValue);

        // Make the asynchronous request and build the response
        // const parsedData = await makeAsyncPostRequest(catchAllValue);

        const localServerData = await makeLocalServerRequest(catchAllValue);
        console.log(localServerData);

        // if (parsedData) {
        // const { assignee, tasks, type } = parsedData;
        // speakOutput = `The tasks for ${assignee} are: ${tasks.join(', ')} under ${type} ToDo-List have been successfully added.`;
        // } else {
        // speakOutput = 'Sorry, there was an error processing your request.';
        // }

        return handlerInput.responseBuilder
            .speak(speakOutput + " Would you like to add another task, or say 'done' to finish?")
            .reprompt('Would you like to add another task?')
            .getResponse();
    }
};


const AddTaskIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest' &&
            Alexa.getIntentName(handlerInput.requestEnvelope) === 'AddTaskIntent';
    },
    handle(handlerInput) {
        let speakOutput = 'What task would you like to add next?';

        const catchAllValue = handlerInput.requestEnvelope.request.intent.slots.catchAll.value;

        const { assignee, tasks, type } = makeSyncPostRequest(catchAllValue);
        speakOutput = `The tasks for ${assignee} are: ${tasks.join(', ')} under ${type} ToDo-List have been successfully added.`;

        return handlerInput.responseBuilder
            .speak(speakOutput + " Would you like to add another task, or say 'done' to finish?")
            .reprompt('Would you like to add another task, or say "done" to finish?')
            .getResponse();
    }
};

const HelpIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.HelpIntent';
    },
    handle(handlerInput) {
        const speakOutput = 'You can say hello to me! How can I help?';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

const CancelAndStopIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && (Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.CancelIntent'
                || Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.StopIntent');
    },
    handle(handlerInput) {
        const speakOutput = 'Alright, let know if you need to add new tasks. Goodbye!';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};
/* *
 * FallbackIntent triggers when a customer says something that doesn’t map to any intents in your skill
 * It must also be defined in the language model (if the locale supports it)
 * This handler can be safely added but will be ingnored in locales that do not support it yet
 * */
const FallbackIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.FallbackIntent';
    },
    handle(handlerInput) {
        const speakOutput = 'Sorry, I don\'t know about that. Please try again.';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
/* *
 * SessionEndedRequest notifies that a session was ended. This handler will be triggered when a currently open
 * session is closed for one of the following reasons: 1) The user says "exit" or "quit". 2) The user does not
 * respond or says something that does not match an intent defined in your voice model. 3) An error occurs
 * */
const SessionEndedRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'SessionEndedRequest';
    },
    handle(handlerInput) {
        console.log(`~~~~ Session ended: ${JSON.stringify(handlerInput.requestEnvelope)}`);
        // Any cleanup logic goes here.
        return handlerInput.responseBuilder.getResponse(); // notice we send an empty response
    }
};
/* *
 * The intent reflector is used for interaction model testing and debugging.
 * It will simply repeat the intent the user said. You can create custom handlers for your intents
 * by defining them above, then also adding them to the request handler chain below
 * */
const IntentReflectorHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest';
    },
    handle(handlerInput) {
        const intentName = Alexa.getIntentName(handlerInput.requestEnvelope);
        const speakOutput = `You just triggered ${intentName}`;

        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt('add a reprompt if you want to keep the session open for the user to respond')
            .getResponse();
    }
};
/**
 * Generic error handling to capture any syntax or routing errors. If you receive an error
 * stating the request handler chain is not found, you have not implemented a handler for
 * the intent being invoked or included it in the skill builder below
 * */
const ErrorHandler = {
    canHandle() {
        return true;
    },
    handle(handlerInput, error) {
        const speakOutput = 'Sorry, I had trouble doing what you asked. Please try again.';
        console.log(`~~~~ Error handled: ${JSON.stringify(error)}`);

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

/**
 * This handler acts as the entry point for your skill, routing all request and response
 * payloads to the handlers above. Make sure any new handlers or interceptors you've
 * defined are included below. The order matters - they're processed top to bottom
 * */
exports.handler = Alexa.SkillBuilders.custom()
    .addRequestHandlers(
        LaunchRequestHandler,
        HelloWorldIntentHandler,
        AddTaskIntentHandler,
        HelpIntentHandler,
        CancelAndStopIntentHandler,
        FallbackIntentHandler,
        SessionEndedRequestHandler,
        IntentReflectorHandler)
    .addErrorHandlers(
        ErrorHandler)
    .withCustomUserAgent('sample/hello-world/v1.2')
    .lambda();

