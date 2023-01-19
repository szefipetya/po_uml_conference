# UML Conference [[gallery]](https://drive.google.com/drive/folders/15k1jUvJAs5WEPvACtb3HFG9gztOGo4bw?usp=share_link)
Uml conference is a Java based webapplication 

## Functionality

- The main functionality is to create, edit and modify class diagrams.

- The diagrams can be shared with other users. 

- Shared diagrams can be edited by multiple users at the same time with the help of *[websockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API).*

## System requirements
- Java 8 JRE
- node (v14 or higher)
- PostgreSQL 13
- Install the http-server package
    | `npm install http-server -g`

## Start on windows
- run the start.bat file
- open up a browser at http://localhost:8100

## Start on Linux
- Run the server | `java -jar ./back-end/target/umlconference-0.0.1-SNAPSHOT.jar`
- Run the http server | `http-server ./frontend/dist/front-end --port=8100`
- open up a browser at http://localhost:8100

The [documentation](https://github.com/szefipetya/po_uml_conference/blob/master/doc/Szakdoli_szoveg_.pdf) is in hungarian language.
