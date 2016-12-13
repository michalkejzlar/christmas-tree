# Christmas tree
Turn on Christmas tree lights remotely.

## What does it do?
This app can turn on DMX lights on christmas tree remotely by sending donation SMS to czech's foundation project.

## Prerequisites
------------
1. Have christmas tree with DMX lights, recording camera and server already setup. If you don't know what DMX is, see https://en.wikipedia.org/wiki/DMX512
2. Lights server listens on Firebase database changes, so make sure you have google-services.json generated from Firebase console and imported to project. Do not add it to Git.
3. Have Wowza or any other streaming platform already setup. Link HLS/DASH/SmoothStreaming link to the app. 
4. Make sure you have `donations-insert.sql` file inside `assets` folder. During first run, app reads it and fills database with records. If you don't have it, see `WebsiteScrapperService` to fetch new records from foundation website.
