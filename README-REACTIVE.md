# Reactive Stuff

## Links
- [Unraveling Project Reactor](https://eherrera.net/project-reactor-course/)
- [Project Reactor](https://projectreactor.io/)
- [Reactive Streams](https://www.reactive-streams.org/)
  - [Textual Description](https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.4/README.md#specification)
  - [API](https://www.reactive-streams.org/reactive-streams-1.0.4-javadoc/org/reactivestreams/package-summary.html)
- [Intellij Reactive Blog](https://blog.jetbrains.com/idea/2023/06/reactive-programming-made-easy/)


## Messaging
Sinks can be used to implement messaging

Two scenarios, a service A providing data service B consumes immediately. This
is the classic flat map scenario. Then a service C which can't be started until
B finishes. That is the scenario, that the whole system needs to be in a state
C can work on, for example B gathers some data into a database and C creates
reports over the data.

A simple example use case with all services working asynchronous:
 - **A**: generates a UUID
 - **B**: splits the UUID into characters and writes them to a file; emits an event containing the file name
 - **C**: counts the characters from the file; emits an event when finished
 - **D**: computes manager bonus based on characters counted

(A --flatMap--> B) --sink--> C --sink--> D