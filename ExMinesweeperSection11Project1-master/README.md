<h1>Section 11 Project 1</h1>

<h2>Overview</h2>

A few exercises prior, we added capability to persist the state of the board. We created a class called ```FilePersistenceStrategy``` to store the state of a board in a file. 

In this project we will learn how to use **JDBC** to save data in a relational database. Once we have implemented this functionality, we will have two ways of saving the state of a board – a file, and a database. Therefore, this feature also brings added choices. The Minesweeper software needs a way to determine which mechanism it should use for saving the Board's state. 

In the past, we have used _config.properties_ to specify the file where the board's state should be stored. Here are the contents of _config.properties_ for your reference.

    persistence.filename=/home/pshah/tmp/jminesweeper.db

_**File Sample 1: config.properties**_

As you can see, config.properties contains only one line of configuration - the name of the file in which data has to be persisted. Till now, file persistence was the only way to save the state of a board, so we specified the filename in which the data had to be saved. We did not need any further configuration. However, now we have one more option – database persistence. Therefore, we need to add an additional property which will determine, what kind of persistence should be used.

We will use a property called ```persistence.strategy``` to specify the persistence strategy.

    persistence.strategy=<fully_qualified_name_of_persistence_class>

The value of this property will determine the persistence strategy to be used. We will explain why we have chosen to use the fully qualified class name of the persistence strategy. But first let us explain an alternative. 

The value of this property could also have been some String that determines which persistence strategy to use. Maybe something like this:

    persistence.strategy=RDBMS

The code which reads this value can have a conditional block that instantiates the appropriate class based on the value.

    if(value.equals("RDBMS")) {
        persistenceStrategy = new JDBCPersistenceStrategy();
    } else if(value.equals("FILE")) {
        persistenceStrategy = new FilePersistenceStrategy();
    }

_**Code Snippet 1**_

Instead of this, if we use the fully qualified class name of the persistence strategy as the value, then we can simply instantiate an insance of that class using reflection.

    persistence.strategy=com.diycomputerscience.minesweeper.FilePersistenceStrategy

_**File Sample 2: config.properties**_

If we use properties as shown above, our code will be a single line which instantiates an instance of persistenceStrategy using reflection, as shown in the pseudo code below.

    persistenceStrategy = //use_reflection_to_instantiate_class

_file sample 2_ suggests that we want to use a file persistence strategy to save the data. If we choose to do this, the ```FilePersistenceStrategy``` class is going to need the name of the file in which data should be saved. Hence, the ```FilePersistenceStrategy``` needs an additional property for the filename in which data will be stored. 

We will provide that information in an another property called ```persistence.filename``` as shown below. 

    persistence.strategy=com.diycomputerscience.minesweeper.FilePersistenceStrategy
    persistence.filename=/home/pshah/tmp/jminesweeper.db
    
_**File Sample 3: config.properties**_    

Had we used the database persistence strategy, we would have needed a different set of additional properties, such as the database url, connection parameters, etc. We will supply these values using keys similar to the one shown for filename.

Specifying a persistence strategy and all the information it needs, is the first part of the puzzle. The next thing we need to figure out is, how do we make these details available to the specific persistence strategy ? Or in other words, what is a good way for the specific persistence strategy to get all the details it needs ? Note the words we have used in the line above - _what is a good way ?_ It's not just about giving information to the persistence strategy, it is about doing it in a way that is _good_ (readable, maintainable, etc). There are several ways of achieving this. We will choose the simplest of these and directly supply an instance of ```Properties``` to the persistence strategy. 

How do we give an instance of Proeprties to the specific ```PersistenceStrategy``` object ? We can either give an instance of ```Properties``` to the constructor of the ```PersistenceStrategy``` when we instantiate it, or alternatively, we can add a method called ```configure(Properties configProps)``` to the interface ```PersistenceStrategy```. We are in not in favor of the first approach, because we cannot enforce every  implementation of ```PersistenceStrategy``` to provide a constructor that takes a ```Properties``` argument. But if we use the second approach, we can definitely ensure that every class that implements ```PersistenceStrategy```, provides a ```configure``` method which takes a ```Properties``` object as argument (otherwise the code will not even compile). This method has the responsibility of configuring it's owning object with appropriate values.

This is what the new ```PersistenceStrategy``` interface looks like.

    package com.diycomputerscience.minesweeper.model;

    public interface PersistenceStrategy {
        public void configure(Properties properties) 
                                      throws ConfigurationException;

	    public void save(Board board) throws PersistenceException;

	    public Board load() throws PersistenceException;
    }

_**Code Snippet 1**_

As you can see, we have added a ```configure``` method to the original interface. This method needs to be invoked with an instance of the ```Properties``` object, created from ```config.properties```. 

While the code is run in production mode, this method will be called from the ```UI``` class, while in test mode, we will call it from the test class' ```setUp()``` method.

If you look at the code carefully, you will notice that we have moved the interface ```PersistenceStrategy``` to a different package. Earlier it was in the package ```com.diycomputerscience.minesweeper```, and now it has been moved to a package called  ```com.diycomputerscience.minesweeper.model``` . We have created this new package for all model classes.

Till now ```PersistenceStrategy``` had only one implementation - ```FilePersistenceStrategy```. In this project, we have also created another implementation called ```JDBCPersistenceStrategy```, which implements the functionality of saving and restoring a ````Board``` from a relation database. ```JDBCPersistenceStrategy``` does not directly interact with the database, but does so through a ```BoardDao``` object. Therefore it needs to be given a ```BoardDao``` implementation, which it will use to actually interact with the database. Take a look at the code in ```JDBCPersistenceStrategy```. It is quite self evident. ```BoardDao``` is an interface which at the moment has one implementation, ```BoardDaoJDBC```. ```JDBCPersistenceStrategy``` uses ```BoardDaoJDBC``` to actually communicate with the database. We decided to implement ```BoardDao``` because we think it will decouple the strategy from the actual database communication code. This is great for explaining the [DAO](http://www.oracle.com/technetwork/java/dataaccessobject-138824.html) design pattern, but in retrospect we will realize that the design was a bit over zealous in creating unnecessary layers. But we will let that wisdom dawn on us later :-)

In this project, you have to implement the ```BoardDaoJDBC``` (an implementation of ```BoardDao```) class which actually interacts with a database. You also have to implement the ```DBInit``` class which creates the initial schema.

This project uses [HSQLDB](http://hsqldb.org/) as the database. HSQLDB is a simple to use database, written in Java. You can check out a [basic tutorial here](http://hsqldb.org/web/hsqlDocsFrame.html).

<h2>Steps For This Activity</h2>

 1. Run ```AllTests``` and notice that 79 tests are run, out of which there are 4 errors and 1 failure.
 1. Write code to fix the errors and failures.
 1. Run ```UI``` in production mode and configure ```config.properties``` to save data in a file and ensure it works - saving the game, closing it, and reloading it.
 1. Study ```JDBCPersistenceStrategy``` and create appropriate key-value pairs in ```config.properties``` for database persistence.
 1. Implement code in ```DBInit``` to create the initial schema (Note: We have provided a jar for using HSQLDB).
 1. Implement ```BoardDaoJDBC``` to implement all the database persistence code
 1. Implement ```UI.buildPersistenceStrategy()``` and verify that the game works in production mode. Be sure to test all the persistence functionality with databases as well as with files.

