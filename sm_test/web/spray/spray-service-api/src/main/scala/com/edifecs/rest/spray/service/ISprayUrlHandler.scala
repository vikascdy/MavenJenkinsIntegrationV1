package com.edifecs.rest.spray.service

import com.edifecs.epp.isc.annotations.{Rest, Arg, AsyncCommand, CommandHandler}
import com.edifecs.epp.isc.async.MessageFuture

import java.lang.Boolean

@CommandHandler
@Rest(enabled=false)
trait ISprayUrlHandler {

  // TODO: Add security to these commands.

  @AsyncCommand(
    name="registerCommandShortcutUrl",
    description="""Creates a new shortcut URL for a command. This shortcut will remain until it is
 deleted using `unregisterCommandShortcutUrl` or until the spray service is stopped.""")
  def registerCommandShortcutUrl(
    @Arg(
      name="url",
      required=true,
      description="The URL to use as a shortcut"
    ) url: String,
    @Arg(
      name="serviceType",
      required=true,
      description="The service type for which `command` is defined"
    ) serviceType: String,
    @Arg(
      name="command",
      required=true,
      description="The command to create a shortcut to"
    ) command: String,
    @Arg(
      name="urlSuffix",
      required=false,
      description="""The URL suffix of all commands made through this shortcut will be this,
 followed by the suffix of the shortcut URL. For example, given `url="foo",
 serviceType="some-service", command="bar", urlSuffix="/a/b"`, the URL `/foo/c` would be a shortcut
 URL for `/rest/service/some-service/bar/a/b/c`."""
    ) urlSuffix: String = null
  ): MessageFuture[Boolean]

  @AsyncCommand(
    name="unregisterCommandShortcutUrl",
    description="""Deletes a shortcut URL that was previously created using
 `registerCommandShortcutUrl`. Return value will be `true` if an existing shortcut was deleted,
 `false` otherwise.""")
  def unregisterCommandShortcutUrl(
    @Arg(
      name="url",
      required=true,
      description="The URL of the shortcut to delete"
    ) url: String
  ): MessageFuture[Boolean]
}
