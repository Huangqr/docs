# Ubantu

**修改下载源**

    1.sudo cp /etc/apt/sources.list /etc/apt/sources.list.bak
    2.sudo vim /etc/apt/sources.list （安装 sudo apt-get install vim）
    
        deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
        deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
        deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
        deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
        deb http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
        deb-src http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
        deb http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
        deb-src http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
        deb http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
        deb-src http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse

    3.sudo apt-get update
    4.sudo apt-get upgrade
