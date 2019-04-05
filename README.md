# CCtray XML (cc.xml) Plugin

This plugin provides the `cc.xml` functionality for Jenkins views that was part of Jenkins core up to version 2.TODO.

## Usage

This plugin adds links to the end of the "RSS bar" below lists of items.
This is a difference from the former Jenkins core implementation, which added links on the builds history page.

The links point to the URLs `/cc.xml/` and `/cc.xml/?recursive` which are similar to those previously in core.
`/cc.xml` now redirects to `/cc.xml/`, so any client that supports HTTP redirects will be able to continue to use old URLs.
