package net.cassite.hottapcassistant.feed;

import net.cassite.hottapcassistant.util.Utils;
import vjson.deserializer.rule.LongRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;

import java.time.Instant;
import java.time.ZoneId;

public class Version1FeedParser implements FeedParser {
    @Override
    public void parse(String input) {
        var entity = Utils.deserializeWithAllFeatures(input, Entity.rule);
        if (entity.program != null) {
            var program = entity.program;
            if (program.version != null) {
                var version = program.version;
                if (version.ver != null) {
                    Feed.feed.latestVersion = version.ver;
                    if (version.ts != 0) {
                        long ts = version.ts;
                        Feed.feed.latestVersionReleaseTime = Instant.ofEpochSecond(ts).atZone(ZoneId.systemDefault());
                    }
                }
            }
        }
    }

    private static class Entity {
        EntityProgram program;

        static Rule<Entity> rule = new ObjectRule<>(Entity::new)
            .put("program", (o, it) -> o.program = it, EntityProgram.rule);
    }

    private static class EntityProgram {
        EntityProgramVersion version;

        static Rule<EntityProgram> rule = new ObjectRule<>(EntityProgram::new)
            .put("version", (o, it) -> o.version = it, EntityProgramVersion.rule);
    }

    private static class EntityProgramVersion {
        String ver;
        long ts;

        static Rule<EntityProgramVersion> rule = new ObjectRule<>(EntityProgramVersion::new)
            .put("ver", (o, it) -> o.ver = it, StringRule.get())
            .put("ts", (o, it) -> o.ts = it, LongRule.get());
    }
}
