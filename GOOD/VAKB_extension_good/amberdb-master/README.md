AmberDB
=======

###Latest AmberDb snapshot version : 2.4.0-SNAPSHOT
###Latest AmberDb release version  : 2.4.0-RELEASE

[<img src="http://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/Ant_in_amber.jpg/320px-Ant_in_amber.jpg" align="right">](http://commons.wikimedia.org/wiki/File:Ant_in_amber.jpg)

[![Build Status](https://travis-ci.org/nla/amberdb.png?branch=master)](https://travis-ci.org/nla/amberdb)
(Huboard: [Tasks](http://huboard.com/nla/amberdb/board),
          [Backlog](http://huboard.com/nla/amberdb/backlog))
([Javadoc](http://nla.github.io/amberdb/apidocs/))

A graph domain model on top of SQL for representing digital library objects and metadata. Supports:

* Suspendable long-running transactions
* History keeping and history subscription for indexing
* Ordered edges


Usage
-----

In-memory:

```java
try (AmberDb db = new AmberDb()) {
    Work work = db.addWork();
}
```

Local filesystem:

```java
try (AmberDb db = new AmberDb(Paths.get("/tmp/mygraph")) {
    Work work = db.addWork();
}
```

Remote JDBC:

```TODO
```

======
