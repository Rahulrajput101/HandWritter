package com.elkdocs.handwritter.domain.utils

import javax.inject.Named

@Named("myFolderContentRepository")
@Retention(AnnotationRetention.RUNTIME)
annotation class FolderRepositoryName

@Named("myImageDetailRepository")
@Retention(AnnotationRetention.RUNTIME)
annotation class ImageRepositoryName