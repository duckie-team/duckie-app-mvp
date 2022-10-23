package land.sungbin.androidprojecttemplate.domain.usecase.fetch

import land.sungbin.androidprojecttemplate.domain.model.Feed
import land.sungbin.androidprojecttemplate.domain.model.Heart
import land.sungbin.androidprojecttemplate.domain.model.util.FK
import land.sungbin.androidprojecttemplate.domain.model.util.PK
import land.sungbin.androidprojecttemplate.domain.model.util.Unsupported
import land.sungbin.androidprojecttemplate.domain.repository.FetchRepository
import land.sungbin.androidprojecttemplate.domain.repository.result.DuckApiResult
import land.sungbin.androidprojecttemplate.domain.repository.result.DuckFetchResult
import land.sungbin.androidprojecttemplate.domain.repository.result.runDuckApiCatching
import land.sungbin.androidprojecttemplate.domain.usecase.fetch.cache.CacheType
import land.sungbin.androidprojecttemplate.domain.usecase.fetch.cache.invokeOrLoadCache

@Unsupported
class FetchHeartUseCase(
    private val repository: FetchRepository,
) {
    /**
     * 주어진 피드가 받은 [좋아요][Heart] 목록을 조회합니다.
     *
     * 등록된 정보가 있다면 [DuckFetchResult.Success] 로 해당 값을 반환하고,
     * 그렇지 않다면 [DuckFetchResult.Empty] 를 반환합니다.
     *
     * 자동으로 캐싱이 적용됩니다.
     *
     * @param feedId 조회할 [피드 아이디][Feed.id]
     * @param force 캐시를 무시하고 요청할지 여부
     * @return 조회된 [좋아요][Heart] 목록을 담은 [fetch 결과][DuckFetchResult]
     */
    suspend operator fun invoke(
        @PK @FK feedId: String,
        force: Boolean = false,
    ): DuckApiResult<Heart> = invokeOrLoadCache(
        type = CacheType.Hearts,
        pk = feedId,
        forceUpdate = force,
    ) {
        runDuckApiCatching {
            repository.fetchHeart(
                feedId = feedId,
            )
        }
    }
}
