# Quests documentation

This directory contains the documentation for Quests. You can 
build it using Jekyll, or view it online at 
[https://quests.leonardobishop.com/](https://quests.leonardobishop.com/).

## Building

To build the documentation, you will need to install Jekyll. You can 
do this by following the instructions on the
[Jekyll website](https://jekyllrb.com/docs/installation/).

Once you have Jekyll installed, you can build the documentation by 
running `jekyll build` in this directory. The documentation will be 
built into the `_site` directory.

```
bundle exec jekyll build
```

Alternatively, you may use the pre-defined docker-compose.yml file to
build and serve the documentation.

```
docker compose up --build
```

## Contributing

See [contributing-to-the-wiki.md](contributing-to-the-wiki.md).
(Online version: 
[https://quests.leonardobishop.com/contributing-to-the-wiki.html](https://quests.leonardobishop.com/contributing-to-the-wiki.html)

## License

This documentation is subject to the same license as Quests itself.