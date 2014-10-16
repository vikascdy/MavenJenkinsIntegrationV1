package com.edifecs.xboard.portal.api

import java.io.{File, FileInputStream, InputStreamReader}
import java.util
import java.util.{Comparator, Collections}
import java.util.concurrent.ConcurrentHashMap

import com.edifecs.epp.security.ISecurityManager
import com.edifecs.xboard.portal.FeaturedItemJsonWrapper
import com.edifecs.xboard.portal.FeaturedItemJsonWrapper.{Link, FeaturedItem, Section}
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.beanutils.BeanUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Created by abhising on 25-08-2014.
 */
object XPFeatureItemDatastore {

  val logger = LoggerFactory.getLogger(getClass)
  private var sections: util.Map[String, Section] = new ConcurrentHashMap[String, Section]()

  def load(files: util.List[File]): Unit = {
    files map (f => {
      logger.debug(s"reading file : ${f.getName}")

      var item: FeaturedItemJsonWrapper = null
      try {
        item = parseJson(f)
        addFeaturedItem(item)
      } catch {
        case ex: Exception => logger.error(s"error parsing file : ${f.getName}", ex)
      }
      logger.debug(s"done reading feature items.")
    })
  }

  def addFeaturedItem(item: FeaturedItemJsonWrapper): Unit = {
    item.getSections map (s => {
      logger.debug(s"reading section : ${s.getName}")
      val sec = sections.get(s.getName)

      if (null == sec) {
        sections += s.getName -> s
      } else {
        sec.getFeaturedItems.addAll(s.getFeaturedItems)
      }
    })
  }

  def parseJson(jsonFile: File): FeaturedItemJsonWrapper = {
    val gson = new Gson()
    val reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)))
    val item = gson.fromJson(reader, classOf[FeaturedItemJsonWrapper]).asInstanceOf[FeaturedItemJsonWrapper]
    item
  }

  def getUserSections(sm: ISecurityManager): util.Collection[Section] = {
    val sectionSet: util.Set[Section] = new util.HashSet[Section]()
    sections map (ss => {
      val s = ss._2
      if ((s.getPermission == null || s.getPermission.trim.isEmpty) || sm.getAuthorizationManager.isPermitted(s
        .getPermission)) {
        val sec = new Section
        sec.setName(s.getName)
        var filteredSet: util.Set[FeaturedItem] = new util.HashSet[FeaturedItem]()
        filteredSet.addAll(
          (s.getFeaturedItems filter (i => if (i.getPermission != null && !i.getPermission.trim.isEmpty) sm
            .getAuthorizationManager.isPermitted(i.getPermission)
          else true)
            map (x => {
            var _s = new FeaturedItem
            _s.setIcon(x.getIcon)
            _s.setDescription(x.getDescription)
            _s.setTitle(x.getTitle)
            _s.setWeight(x.getWeight)
            _s.setPermission(null)
            _s.setLinks(x.getLinks.filter(l => if (l.getPermission != null && !l.getPermission.trim.isEmpty) sm
              .getAuthorizationManager.isPermitted(l.getPermission)
            else true) map (l => {
              var _l = new Link
              BeanUtils.copyProperties(_l, l)
              _l.setPermission(null)
              _l
            }))
            _s
          })
            ))


        val set: util.Set[FeaturedItem] = new util.TreeSet[FeaturedItem](new Comparator[FeaturedItem] {
          override def compare(o1: FeaturedItem, o2: FeaturedItem): Int = {
            if (o1.getWeight == o2.getWeight) 1
            else
              o2.getWeight - o1.getWeight
          }
        })
        set.addAll(filteredSet)
        sec.setFeaturedItems(set)
        sectionSet.add(sec)
      }
    })
    sectionSet
  }
}
