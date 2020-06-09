## Maven

**手动发布包到nexus**

    mvn deploy:deploy-file -DgroupId=${} -DartifactId=${} -Dversion=1.0.0 -Dpackaging=jar -Dfile=./jar/xxx.jar  -Durl=http://nexus/repository/3rd_part/ -DrepositoryId=maven-3rd_part
   
**安装jar包到本地**
   
    mvn install:install-file -DgroupId=${} -DartifactId=${} -Dversion=1.0.0 -Dpackaging=jar  -Dfile=./jar/xxx.jar 


