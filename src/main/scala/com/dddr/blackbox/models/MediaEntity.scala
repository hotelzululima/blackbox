package com.dddr.blackbox.models

import java.util.UUID

/**
 * Created by rroche on 9/16/15.
 */

sealed trait MediaType { def media: String }
case class TextMedia(media: String = "text/plain") extends MediaType
case class ImageMedia(media: String = "image/*") extends MediaType
case class VideoMedia(media: String = "video/*") extends MediaType
case class BinaryMedia(media: String = "application/octet-stream") extends MediaType

case class MediaEntity(id: MediaId = MediaId(),
                       boxId: BoxId,
                       mediaId: String = UUID.randomUUID().toString(),
                       contentType:  MediaType = BinaryMedia())

case class ConvertMediaType(stringType: String) {
  /**
   * takes in the content-type and returns one of the MediaTypes
   * Currently MediaType does not differentiate between different images/videos
   * @return MediaType
   */

  def getMedia: MediaType = {
    if (stringType.contains("text")) {
      TextMedia()
    } else if (stringType.contains("image")) {
      ImageMedia()
    } else if (stringType.contains("video")) {
      VideoMedia()
    } else {
      BinaryMedia()
    }
  }
}
