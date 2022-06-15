package com.cloud.encrypting_cloud_storage.controller;

import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import com.cloud.encrypting_cloud_storage.models.ApiResponse;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/24
 * @version: 1.0
 */
@RestController
@RequestMapping("/files/{userId}")
@Api(tags = "文件控制器", value = "文件管理")
public class FileController extends BaseController{
    @GetMapping
    @ApiOperation(value = "获取用户根目录及目录下的文件，采用递归方式")
    public ApiResponse getRoot(@PathVariable("userId") Long userId){
        List<FilePo> filePos = fileService.findAllFileByUserAndDir(new UserPo(userId), new FilePo(0L));
        return ApiResponse.ofSuccess(filePos);
    }

    /**
     * 获取当前文件下的所有文件以及目录

     */
    @GetMapping("/{dirInode}")
    @ApiOperation(value = "获取目录及目录下的文件，采用递归方式")
    public ApiResponse getFiles(@PathVariable("userId") Long userId,@PathVariable("dirInode") Long dirInode) {
        FilePo dir = fileService.findFileByInodeAndUserId(dirInode, userId);
        return ApiResponse.ofSuccess(dir);
    }

    /**
     * 删除文件或者目录。该方法会删除目录，以及目录下的所有文件，以及相关文件的块信息
     *
     */
    @DeleteMapping("/{dirInode}")
    @ApiOperation(value = "删除目录，以及目录下的文件，以及文件对应的文件块元数据")
    public ApiResponse deleteDir(@PathVariable("userId") Long userId,@PathVariable("dirInode") Long dirInode) {
        fileService.deleteByInodeAndUser(dirInode,userId);
        // TODO 删除目录，以及目录下的所有文件块缓存，如果文件块缓存量为0，还需要清除具体的数据
        return ApiResponse.ofSuccess();
    }

    /**
     * 修改文件或者目录信息
     *
     * @return
     */
    @PutMapping("/{dirInode}")
    public ApiResponse putDir(@PathVariable("userId") Long userId,@PathVariable("dirInode") Long dirInode,@RequestBody FilePo filePo) {
        boolean b = fileService.exitsMyFile(dirInode);
        if(!b){
            return ApiResponse.ofStatus(StatusEnum.NO_RESOURCES);
        }
        filePo.setInode(userId);
        FilePo save = fileService.save(filePo);
        return ApiResponse.ofSuccess(save);
    }

    /**
     * 在当前目录下，新建目录或者文件
     */
    @PostMapping("/{dirInode}")
    public ApiResponse newDir(@PathVariable("userId") Long userId,@PathVariable("dirInode") Long dirInode,@RequestBody FilePo filePo) {
        filePo.setUser(new UserPo(userId));
        filePo.setParentDir(new FilePo(dirInode));
        FilePo save = fileService.save(filePo);
        return ApiResponse.ofSuccess(save);
    }

}
