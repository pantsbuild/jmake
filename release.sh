#!/bin/bash

function die() {
    echo "$1"
    exit 1
}

test -e .git || die "must be run from the root of a jmake git checkout"
test -e build.xml || die "must be run from the root of a jmake git checkout"
test -e jmake.pom || die "must be run from the root of a jmake git checkout"

ant package_for_release || die "failed to package"

version=$(java -jar dist/release/jmake.jar -version 2>/dev/null | cut -d ' ' -f 3)

test -n "$version" || die "Could not get version"

tag=$(git rev-parse HEAD)

cat jmake.pom | sed "s/%VERSION%/$version/" | sed "s/%TAG%/$tag/" > dist/release/jmake.pom || die "failed to create pom file"

cd dist/release

mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=ossrh -Dfile=jmake.jar -DgroupId=org.pantsbuild -DartifactId=jmake -Dversion=$version -DpomFile=jmake.pom || die "Failed to deploy jmake.jar"

mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=ossrh -Dfile=jmake-sources.jar -DgroupId=org.pantsbuild -DartifactId=jmake -Dversion=$version -DpomFile=jmake.pom -Dclassifier=sources || die "Failed to deploy jmake-sources.jar"

mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=ossrh -Dfile=jmake-javadoc.jar -DgroupId=org.pantsbuild -DartifactId=jmake -Dversion=$version -DpomFile=jmake.pom -Dclassifier=javadoc || die "Failed to deploy jmake-javadoc.jar"

