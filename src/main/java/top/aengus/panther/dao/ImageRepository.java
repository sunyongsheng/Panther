package top.aengus.panther.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import top.aengus.panther.model.image.ImageModel;
import top.aengus.panther.model.image.UploadCount;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageRepository extends PagingAndSortingRepository<ImageModel, Long> {

    long countAllByOwner(String owner);

    long countAllByUploadTimeAfterAndUploadTimeBefore(Long startTime, Long endTime);

    List<ImageModel> findAllByOwner(String appKey);

    List<ImageModel> findAllByOwnerAndStatus(String appKey, Integer status);

    Page<ImageModel> findAllByOwner(String appKey, Pageable pageable);

    Page<ImageModel> findAllByStatus(Integer status, Pageable pageable);

    ImageModel findByRelativePath(String path);

    @Query(value = "SELECT owner AS appKey,COUNT(*) AS amount FROM tb_image GROUP BY owner ORDER BY amount DESC LIMIT :count", nativeQuery = true)
    List<UploadCount> findAppKeyOrderByUploadCount(@Param(value = "count") int count);
}
