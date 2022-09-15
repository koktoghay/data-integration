# spring-data整合示例

## Redis
### 集群架构
#### redis1
* master     127.0.0.1:6379 
* replicate  127.0.0.1:6380
* replicate  127.0.0.1:6381

#### redis2
* master     127.0.0.1:36379
* replicate  127.0.0.1:36380
* replicate  127.0.0.1:36381

#### sentinel（监控以上两套redis主从服务）
* sentinel   127.0.0.1:26379
* sentinel   127.0.0.1:26380
* sentinel   127.0.0.1:26381

#### sentinel_xxx.conf
* sentinel monitor master_6379 127.0.0.1 6379 2
* sentinel monitor master_36379 127.0.0.1 36379 2\
此处本地ip应改成具体的主机ip


### MultiRedisTemplateConfiguration多数据源配置（单机配置）
#### 测试场景一 测试连接，并设置key值
1. 出现连接错误：修改redis启动配置文件，将bind注释。(默认bind仅能本地连接)

#### 测试场景二 杀死master后，服务是否正常
1. 手动停止master(6379)服务后，哨兵选主成功后，redis服务恢复正常，6381接替6379成为新的master，而6380重新追随6381
2. 手动重启6379后，也将重新追随新的master(6381)
3. 应用程序无法自动故障恢复，因此在哨兵配置架构下，应用程序采用单机配置存在弊端

### SentinelRedisTemplateConfiguration
#### 测试场景一 测试连接，并设置key值
1.出现连接错误：哨兵配置sentinel_xxx.conf中sentinel monitor master_6379 127.0.0.1 6379 2 主节点应该是具体主机ip，而非本地ip。主要是通过redis哨兵发现主服务ip，因此不能是本地ip

#### 测试场景二 
1. 手动停止master(6379)服务后，哨兵选主成功后，redis服务恢复正常，6381接替6379成为新的master，而6380重新追随6381
2. 手动重启6379后，也将重新追随新的master(6381)
3. 应用程序自动故障恢复，因此在哨兵配置架构下，应用程序采用哨兵配置能够自动重连master，从而避免应用故障

### ClusterRedisTemplateConfiguration
