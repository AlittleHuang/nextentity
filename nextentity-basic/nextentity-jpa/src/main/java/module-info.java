module nextentity.jpa {
    requires static org.jetbrains.annotations;

    requires nextentity.core;
    requires jakarta.persistence;
    requires nextentity.jdbc;
    requires static lombok;
    requires static org.slf4j;

    exports io.github.nextentity.jpa;
}