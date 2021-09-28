package top.aengus.panther.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import top.aengus.panther.model.image.ImageModel;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageRepository extends PagingAndSortingRepository<ImageModel, Long> {

    long countAllByCreator(String creator);

    ImageModel findBySaveName(String saveName);

    ImageModel findByIdAndCreator(Long id, String creator);

    ImageModel findByAbsolutePath(String absolutePath);

    List<ImageModel> findAllByOwner(String appKey);

    Page<ImageModel> findAllByOwner(String appKey, Pageable pageable);

}
