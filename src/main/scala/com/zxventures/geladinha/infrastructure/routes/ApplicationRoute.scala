package com.zxventures.geladinha.infrastructure.routes

import com.zxventures.geladinha.infrastructure.serialization.ApplicationMarshalling

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
}
