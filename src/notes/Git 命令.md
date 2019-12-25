**Git**

    1.git checkout -b <branchName> origin   开分支
    
    2.git rm -r --cached  "bin/"    删除文件管理的命令.
    
    3.git commit
      git stash save (name)     name可不设置,不设置时不写save
      git pull 
      git push
      git stash list    本地缓存列表
      git stash apply(pop) stash@{index}       index为缓存索引角标，使用pop为弹出stash、使用后将删除当前stash
    
    4.git stash drop stash@{index}    可删除本地缓存，index同apply
    
    5.git branch -a  查看所有分支
    
    6.git branch -d(-D) <branchName>   删除本地分支（D强制删除）
    
    7.git push origin --delete <branchName>  删除远程分支
    
    8.git branch -m <old_branchName> <new_branchName>  修改本地分支名称
    
    9.git push --set-upstream origin < new_branchName >  将本地新分支推送到远程（需先删除远程同名分支）
    
    10.git branch <new-branch-name> <tag-name>  根据tag创建新的分支
    
    11.git push origin v0.1.2  # 将v0.1.2 Tag提交到git服务器
    
    12.git push origin --tags  # 将本地所有Tag一次性提交到git服务器
    
    13.将分支推送到远程存储库时遇到错误: Git failed with a fatal error.
       the remote end hung up unexpectedly
       the remote end hung up unexpectedly
       RPC failed; curl 56 OpenSSL SSL_read: SSL_ERROR_SYSCALL, errno 10054
       Pushing to https://github.com/xxxxxx/center
       Everything up-to-date
       
       在git里配置
      
       $ git config http.sslVerify "false" 或者 $ git config --global http.sslVerify "false"
       $ git config http.postBuffer 524288000  |  git config https.postBuffer 524288000
       $ git pull origin master --allow-unrelated-histories 拉取出错

    14.git tag tagName  打标签（git tag -a tagName -m "my version 1.4"）
       git tag  列出所有标签
       git push origin --tags   推送所有tag到远程仓库(git push origin tagName    推送单一标签)
       git tag -l 'v1.8.5*'     查找标签
       git tag -d tagName       删除标签
       git push origin :refs/tags/tagName       删除远程标签
       git tag -a tagName 9fceb02(提交的校验和)       指定提交记录打标签