package scales

abstract trait HttpMethod

trait GET extends HttpMethod
trait POST extends HttpMethod
trait PUT extends HttpMethod
trait DELETE extends HttpMethod
trait HEAD extends HttpMethod
trait OPTIONS extends HttpMethod
trait CONNECT extends HttpMethod
trait TRACE extends HttpMethod