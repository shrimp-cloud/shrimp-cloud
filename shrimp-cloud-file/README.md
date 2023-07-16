# 文件服务

## 使用

### 添加依赖

```xml
<dependency>
    <groupId>com.wkclz.file</groupId>
    <artifactId>shrimp-cloud-file</artifactId>
    <version>${latest.version}</version>
</dependency>
```

### 添加配置

```text
shrimp:
  cloud:
    file:
      s3:
        endpoint: s3-endpoint
        access-key-id: access-key-id
        secret-key: secret-key
        bucket: bucket
        region: region
```

### 上传文件
```java
@RestController
public class TestRest {
    @Autowired
    private S3Api s3Api;

    @PostMapping("/public/s3/upload")
    public Result s3Upload(@RequestParam("file") MultipartFile file, String busnessType){
        String dd = s3Api.upload(file, busnessType);
        return Result.data(dd);
    }
}


```