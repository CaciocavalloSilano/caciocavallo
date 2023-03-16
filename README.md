# Caciocavallo headless Swing UI testing

[![Actions Status](https://github.com/CaciocavalloSilano/caciocavallo/workflows/Java%20CI/badge.svg)](https://github.com/CaciocavalloSilano/caciocavallo/actions)
[![License](https://img.shields.io/github/license/CaciocavalloSilano/caciocavallo.svg)](https://raw.githubusercontent.com/CaciocavalloSilano/caciocavallo/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.caciocavallosilano/cacio-tta/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.caciocavallosilano/cacio-tta)

## Introduction

One problem with running GUI tests is that they need to create windows, grab keyboard focus, and do all sorts of interaction with the screen. 

This has several disadvantages:
 * When running on a developer machine, it requires to not touch anything while running the test suite, otherwise tests may fail or get blocked, 
 e.g. because keyboard focus cannot be acquired or a test window suddenly goes into the background when it should not, etc. 
 This can be quite annoying, especially with large test suites. Also you can not use a debugger since focus will end up in the IDE when stepping through code.
 
 * When running on a Unix-based CI server, it’s possible to utilize Xvfb driver to create a virtual framebuffer, 
 but there are problems that seem to come from asynchronous behaviour of the X architecture. 
 For example, a test would draw a border in RED, and when checking some pixels to be red, they are not. 
 They would only turn red after some unknown time, when the drawing commands have been processed by the X server and graphics card.
 * When running on Windows based CI server, this sort of asynchronous problems don’t occur, but to make up for it, Windows has its own share of problems. 
 First of all, on Windows you need a real screen/graphics card to be present (bad on servers). 
 Even worse, on many Windows servers, you need a user to be logged in, and stay logged in, and the CI server running in that session to be able to access the screen. 
 On other servers, multiple concurrent logons are possible, but not sharing a screen, e.g. when some guy logs into the CI server to do some admin work, 
 it would grab the screen from the CI user, etc. All very complicated and time-consuming to set up. Even popups for Windows security updates will disrupt tests.

This is where Cacio-tta comes into play. It provides a graphics stack for the Java VM, that is completely independent of the environment. 
It renders everything into a virtual screen, which is simply a BufferedImage, and is driven solely by java.awt.Robot events. 
This makes it a perfect fit for GUI testing environments.

## Versions

The goal is to support all LTS OpenJDK releases.

| Version | JDK   |
|---------|-------|
| 1.10    | JDK8  |
| 1.11    | JDK11 |
| 1.17    | JDK17 |

<sub><sup>Earlier JDKs should use `net.java.openjdk.cacio` releases</sup></sub>

## Usage

1. Include Cacio in your Maven dependencies
Simply add the following in your `pom.xml`:
```xml
<dependency>
  <groupId>com.github.caciocavallosilano</groupId>
  <artifactId>cacio-tta</artifactId>
  <scope>test</scope>
</dependency>
```

Or to your `build.gradle`

```groovy
testCompile 'com.github.caciocavallosilano:cacio-tta:1.+'
```

2. Run your test
Add the following annotation to your test class:

`@CacioTest`

If you don't want to take a screenshot on failure using AssertJ Swing, use this:

`@ExtendWith(CacioExtension.class)`

These annotations will make your test run in a Cacio-tta virtual screen environment

3. Optionally, run the whole test suite in Cacio
In some cases, it may be necessary to run the whole test suite in Cacio-tta. In order to do so, add the following to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.12</version>
      <configuration>
        <systemPropertyVariables>
          <java.awt.headless>false</java.awt.headless>
          <awt.toolkit>com.github.caciocavallosilano.cacio.ctc.CTCToolkit</awt.toolkit>
          <java.awt.graphicsenv>com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment</java.awt.graphicsenv>
        </systemPropertyVariables>
      </configuration>
    </plugin>
```

Or to your `build.gradle`

```groovy
test {
    systemProperty "awt.toolkit", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit"
    systemProperty "java.awt.graphicsenv", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment"
}
```
This makes sure that Cacio is loaded instead of the default toolkit. This may be necessary, if any of your tests load any AWT, Java2d or Swing class, and are not annotated with the above annotation. 
This is because Java only allows to set the toolkit once, and it cannot be unloaded or unset. When you load any GUI class before loading the CacioTestRunner, the default toolkit will be loaded, and tests will not run in Cacio.

You can change the resolution of the virtual screen by setting the `cacio.managed.screensize` system property.

For example:
```java
System.setProperty("cacio.managed.screensize", "1920x1080");
```

## Checkout the code

Ok, if you are reading this README chances are that you already got the sources
so you can skip this section.

In case you found this README somewhere else, the best way to get access
to the sources is to clone the main repository:

https://github.com/CaciocavalloSilano/caciocavallo

## Build

Building Cacio requires OpenJDK.

Caciocavallo utilizes Maven for the main modules. In the toplevel Cacio
directory simply type:

`mvn clean install`

This should compile Caciocavallo.

## History

`todo: dead links`

Caciocavallo - Portable GUI backends is one of the finalists of the
OpenJDK Innovators Challenge. As of March 17, 2008 (well, more or
less), Caciocavallo was selected as a finalist to the OpenJDK
challenge, so we even have a cool new website with nothing in it :)

https://openjdk.java.net/projects/caciocavallo/

Here is the mail with the original proposal:

http://mail.openjdk.java.net/pipermail/challenge-discuss/2008-March/000082.html

The project classified Bronze at the Challenge, giving us great honours and
proud :)

http://www.reuters.com/article/pressRelease/idUS134139+29-Sep-2008+BW20080929

You can find the interview with the original developers on this link:

http://mediacast.sun.com/users/robilad/media/daliboropenJDKwinners.ogg

The interview was conducted by a nice guy who's name is known by everyone in
the Free Java community, Dalibor Topic (robilad on IRC). Visit this link
for some other cool interviews:

http://robilad.livejournal.com/37607.html

A short presentation about Cacio was shoot at FOSDEM 2009 by Andrew John Hughes.
You can find it here:

http://www.jroller.com/neugens/entry/cacio_presentation_at_fosdem_2009

Good resource of information are the authors' blogs:

 * http://kennke.org/blog/
 * http://www.jroller.com/neugens
 * http://weblogs.java.net/blog/thetan/

It was migrated to GitHub October 2019 and changed to just support headless Swing testing and support was added for LTS OpenJDK releases.
This commit 314a544b4970d1108be99adbd3f9c5e70a102b3f contains the original code that was converted from the original OpenJDK hg repository.


## See also

[AssertJ Swing](https://joel-costigliola.github.io/assertj/assertj-swing.html)
