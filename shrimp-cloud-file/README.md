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

### 添加S3配置

```text
shrimp:
  cloud:
    file:
      s3:
        endpoint: s3-endpoint
        access-key-id: access-key-id
        secret-key-secret: secret-key-secret
        bucket: bucket
        region: region
```


### 添加AliOSS配置

```text
shrimp:
  cloud:
    file:
      alioss:
        inner-endpoint: s3-endpoint
        outer-endpoint: s3-endpoint
        access-key-id: access-key-id
        secret-key-secret: secret-key-secret
        bucket: bucket
```


### 上传文件
```java
@RestController
public class TestRest {
    
    @Autowired
    private FileApi fileApi;

    @PostMapping("/public/file/upload")
    public Result fileUpload(@RequestParam("file") MultipartFile file, String busnessType){
        String dd = fileApi.upload(file, busnessType);
        return Result.data(dd);
    }
}


```


