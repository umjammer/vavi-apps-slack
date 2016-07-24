# vavi-apps-slack
vavi-apps-slack is a Java application that can export Slack messages incremantally.

## Depends
 * [Simple Slack API](https://github.com/Ullink/simple-slack-api) (Creative Commons CC0 1.0 Universal)
 * [Gson](https://github.com/google/gson) (Apache 2.0)

## Howto

 * get a token from https://api.slack.com/web (push `Generate test tokens` button)
 * prepare a setting file
```shell
$vi ~/.vavi_apps_slack.properties
# your group name + keyword
YOUR_GROUP.user=your_user_name
YOUR_GROUP.token=xoxp-289999999915-12UUYYYYYY9-6XXXXXXXX-999999999999
```
 * run vavi-apps-slack
 * get messages
```shell
$tree tmp
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

## License

vavi-apps-slack is released under the [Apache 2.0 license](https://github.com/google/gson/blob/master/LICENSE).

```
Copyright 2016 Naohide Sano.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
