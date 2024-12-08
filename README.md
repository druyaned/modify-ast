# Modify AST

By using annotations of this project Java source code (abstract syntax trees)
can be modified, e.g. getters can be added, or some print statements can be inserted
at the beginning of method and before each return statement of method's body.

The idea of the project came to me when I had been learning the second volume
of Horstmann's book "[Core Java](https://horstmann.com/corejava/index.html)".
I was disagree with Java's concept of prohibition for annotation processors
to modify Java source files.
Then I stumbled upon [Project Lombok](https://projectlombok.org/contributing/lombok-execution-path).
It revealed for me [openjdk's compilation overview](https://openjdk.org/groups/compiler/doc/compilation-overview/index.html).
During seeking for different solutions I read quite a lot
[posts in stackoverflow](https://stackoverflow.com/questions/65380359),
analyzed much source files from github, dug through Java documentations.
Also I wrote and tested some pleasant code.

So the project uses internal API of jdk.compiler module and should be configured correctly.

## Configuration (Maven)

1. Add the dependency.
```
<dependency>
  <groupId>druyaned</groupId>
  <artifactId>modify-ast</artifactId>
  <version>1.0</version>
</dependency>
```
1. In maven-compiler-plugin add the following configuration.
```
<configuration>
  <fork>true</fork>
  <compilerArgs>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED</arg>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
    <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED</arg>
    <arg>-J--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED</arg>
  </compilerArgs>
  <annotationProcessorPaths>
    <path>
      <groupId>druyaned</groupId>
      <artifactId>modify-ast</artifactId>
      <version>1.0</version>
    </path>
  </annotationProcessorPaths>
  <annotationProcessors>
    <processor>druyaned.modifyast.ModificationProcessor</processor>
  </annotationProcessors>
</configuration>
```

## Usage Example

Person.java
```
import druyaned.modifyast.anno.Enexrint;
import druyaned.modifyast.anno.EnexrintAll;
import druyaned.modifyast.anno.GetterAll;
import java.time.LocalDate;

@GetterAll
@EnexrintAll
public class Person {
    
    private final String name;
    private final LocalDate birthdate;
    
    public Person(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
        return; // for @EnexrintAll
    }
    
    @Enexrint(ignore = true)
    @Override
    public String toString() {
        return "Person{name=" + name + ", birthdate=" + birthdate + '}';
    }
    
}
```
PersonTest.java
```
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

public class PersonTest {
    
    public static void main(String[] args) {
        try {
            // constants
            final String nameConst = "Ivan";
            final LocalDate birthdateConst = LocalDate.of(1990, 1, 1);
            // getters
            Method getName = Person.class.getDeclaredMethod("getName");
            Method getBirthdate = Person.class.getDeclaredMethod("getBirthdate");
            Person person = new Person(nameConst, birthdateConst);
            Object name = getName.invoke(person);
            Object birthdate = getBirthdate.invoke(person);
            // assertions
            String getNameFailure = "name=" + name + " nameConst=" + nameConst;
            String getBirthdateFailure = "birthdate=" + birthdate
                    + " birthdateConst=" + birthdateConst;
            assert name.equals(nameConst) : getNameFailure;
            assert birthdate.equals(birthdateConst) : getBirthdateFailure;
            // report
            System.out.println("Success!");
            System.out.println("name:      " + name);
            System.out.println("birthdate: " + birthdate);
            System.out.println("person=" + person);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            System.out.println("Failure!");
            ex.printStackTrace();
            return;
        }
    }
    
}
```
Output
```
[ENTER Person]
[EXIT Person]
[ENTER getName]
[EXIT getName]
[ENTER getBirthdate]
[EXIT getBirthdate]
Success!
name:      Ivan
birthdate: 1990-01-01
person=Person{name=Ivan, birthdate=1990-01-01}
```
