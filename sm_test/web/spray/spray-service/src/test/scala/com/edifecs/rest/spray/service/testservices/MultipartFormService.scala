package com.edifecs.rest.spray.service.testservices

import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.core.command.AbstractCommandHandler
import com.edifecs.epp.isc.stream.MessageStream
import com.edifecs.servicemanager.annotations.{Handler, Service}
import com.edifecs.servicemanager.api.AbstractService

@Service(name = "multipart",
         version = "1.0",
         description = "Used to test the REST interface.")
class MultipartFormService extends AbstractService {

  override def start() = println("Multipart form service started.")

  override def stop() = println("Multipart form service stopped.")

  @Handler
  def handler: MultipartFormCommandHandler = new MultipartFormCommandHandler()
}

@Akka(enabled = true)
@Rest(enabled = true)
@CommandHandler
class MultipartFormCommandHandler extends AbstractCommandHandler {

  @AsyncCommand
  def form(): MessageFuture[MessageStream] =
    MessageFuture(MessageStream.fromXml(
      <html>
        <head><title>Multipart File Upload</title></head>
        <body>
          <h1>Multipart File Upload</h1>
          <hr />
          <form method="post" action="uploadTwoFiles" enctype="multipart/form-data">
            <label for="file1">File 1:</label>
            <input type="file" name="file1" id="file1" />
            <br />
            <label for="file2">File 2:</label>
            <input type="file" name="file2" id="file2" />
            <br />
            <input type="submit" name="submit" value="Submit" />
          </form>
        </body>
      </html>,
      httpContentType = "text/html", xmlDecl = false))

  @AsyncCommand
  def uploadTwoFiles(
    @Arg(name = "file1", required = true) file1: MessageStream,
    @Arg(name = "file2", required = true) file2: MessageStream
  ): MessageFuture[MessageStream] =
    file1.fold("")((str, c) => MessageFuture(str + c.decodeString("utf-8"))).then { str1 =>
      file2.fold("")((str, c) => MessageFuture(str + c.decodeString("utf-8"))).then { str2 =>
        MessageFuture(MessageStream.fromXml(
          <html>
            <head>
              <title>Uploaded Files</title>
              <style>{"code {display: block; white-space: pre;}"}</style>
            </head>
            <body>
              <h1>Uploaded Files</h1>
              <hr />
              <section>
                <h2>File 1</h2>
                <code id="file1">{str1}</code>
              </section>
              <section>
                <h2>File 2</h2>
                <code id="file2">{str2}</code>
              </section>
            </body>
          </html>,
          httpContentType = "text/html", xmlDecl = false))
      }
    }
}
