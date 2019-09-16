FROM harbor.holdzone.cn/library/centos/centos7-java-1.8:1
MAINTAINER Youwei Du(email:249952455@qq.com)
ENV REFRESHED_AT 7/9/2018 9:50

RUN mkdir -p /root/app/config_center

ADD ./holder-saas-config-center-0.0.1-SNAPSHOT.jar /root/app/config_center

ENV RUN_HOME /root/app/config_center

EXPOSE 8762

COPY docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

