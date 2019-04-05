# Before reading

My colleague Aldo Valdez found this more performant and simple solution (that could also be implemented with a DBMS) with Elastic Search:

https://www.lovelysystems.com/blog/es-iid-generator/

# sample-springtest-dynsequences

This sample project exemplifies a couple of things:

1. How to create dynamic auto-increment sequences with JPA, without DB sequences, so we can create on the fly new sequence names without the burden to the DB that it could be to create potentially too many actual DB sequences.
2. Some Spring Test samples.

DB sequences are a more performant and simpler option if there is a finite small number of them, besides entity IDs sequence shouldn't matter to the business logic, as long as there's a unique way to identify identities, the specific value should not matter. Nonetheless, if there is a need for the generated number to be seen by the end user and respect the sequence among diferent items, besides the potential need of having too many different sequences, this approach might help, but I insist, it **could still be not the best-performant** if simultaneous accesses to the same sequences increase and are constant, since it is relying on table's record transactional locks. It was implemented with optimistic locks, but it could easily be implemented with pessimistic locks if you check the commented out lines inside the source code.

# Automated tests

This project comes out of the shelf with automated tests that proof the solution and are automatically ran while building it. If build finishes with a SUCCESSFUL status, it means the tests were executed with no issues. Have a look at the source code in order to see how the sequence service works. To build the project, clone it or fork it, move to the project folder and run:

```
./gradlew build
```

That's it.

# Startup DB for manual testing

In this example, we are going to use a dockerized Postgresql:

```
sudo docker run -d --name test-postgresql -p 5432:5432 -v $PWD/postgresql:/var/lib/postgresql/data -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

And create sequences table that can be found on **V1__db.sql**

Default user is **postgres** and password was defined by the parameter POSTGRES_PASSWORD, if you change it, make sure you also changed it on application.properties, in the source code.

Once your Postgresql is running, you can use MyService (you better rename it) to generate sequences:

1. Add sequence id to sequences DB table
2. Call service's *nextSeqVal* with the id of the sequence defined in step 1.


# Notes

This project is exemplifying two identical simple tests under the tests folder against two different databases and datasources. In a real scenario, it might be a better idea to have only one datasource.
