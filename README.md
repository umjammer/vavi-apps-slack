# vavi-apps-slack
*vavi-apps-slack* is a Java application that can export Slack messages **incrementally**.

we can find some Slack exporters, but those exports whole messages and channels everytime. 
this application is useful for daily backup.

## Depends
 * [Simple Slack API](https://github.com/Ullink/simple-slack-api) (Creative Commons CC0 1.0 Universal)
 * [Gson](https://github.com/google/gson) (Apache 2.0)

## How To

 * get a token from https://api.slack.com/web (push `Generate test tokens` button)
 * prepare a setting file
```shell
$ vi ~/.vavi_apps_slack.properties
# your group name + keyword
YOUR_GROUP.user=your_user_name
YOUR_GROUP.token=xoxp-289999999915-12UUYYYYYY9-6XXXXXXXX-999999999999
```
 * download vavi-apps-slack
```shell
$ git clone https://github.com/umjammer/vavi-apps-slack.git
```
 * run vavi-apps-slack
```shell
$ cd vavi-apps-slack
$ ./gradlew run -Pargs="YOUR_GROUP"
```
 * get messages
```shell
$ tree tmp
tmp
└── YOUR_GROUP
    ├── channel
    │   ├── channel 1.json
    │   └── channel 2.json
    ├── direct
    │   ├── your friend a.json
    │   ├── your friend b.json
    │   ├── slackbot.json
    │   └── your friend c.json
    └── private
        ├── mpdm-friend1--friend2--you-1.json
        └── mpdm-friend3--friend4--you--friend5-1.json
```
 * view messages
```shell
$ pwd
vavi-apps-slack
$ ./gradlew execute
:compileJava UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:execute
welcome to slack viewer: 4567
> Building 75% > :execute
```
access `http://localhost:4567/`

## License

vavi-apps-slack is released under the [Apache 2.0 license](https://github.com/umjammer/vavi-apps-slack/blob/master/LICENSE).
