# Beacon Upgrade

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/beaconupgrade/banner.png)

## Introduction

This mod expands Minecraft's vanilla beacon system into a more flexible, configurable, and progression-focused mechanic.

Vanilla beacons are simple: build a pyramid, choose one or two effects, and every valid mineral block behaves the same. This mod keeps the familiar beacon foundation, but makes the material choice, payment item, effect selection, effect strength, range, and valid targets matter.

## What changes compared to vanilla?

Beacon Upgrade replaces the vanilla beacon menu and effect logic with an upgraded system built around three main ideas:

- Beacon pyramid materials have different properties.
- Beacon effects are no longer limited to the vanilla primary/secondary layout.
- Data packs can configure beacon base blocks, payment items, available effects, and valid targets.

The usual vanilla requirements still matter: the beacon needs a valid pyramid below it and an unobstructed beam path. However, once active, the beacon behaves very differently.

## Pyramid levels and pyramid strength

Vanilla beacons care only about how many complete pyramid layers exist, up to four layers. Beacon Upgrade supports up to **five pyramid layers**.

Each valid layer still uses the familiar square layout:

| Pyramid level | Layer size |
|---------------|------------|
| 1             | 3×3        |
| 2             | 5×5        |
| 3             | 7×7        |
| 4             | 9×9        |
| 5             | 11×11      |

The important difference is that every beacon base block can now provide:

- **Pyramid strength**
- **Effective radius**

A beacon's total strength and range are calculated from the completed pyramid layers below it.

### Mixed-material pyramids

Mixed pyramids are supported, but each individual layer is evaluated by its weakest block.

For each completed pyramid layer, the beacon looks at all blocks in that layer and uses the block with the lowest maximum pyramid strength as the representative block for that layer.

This means a single weaker block in a layer can reduce what that whole layer contributes.

### Default base block values

Beacon Upgrade configures the vanilla beacon materials as follows:

| Block           | Effective radius per layer | Pyramid strength                 |
|-----------------|----------------------------|----------------------------------|
| Copper blocks   | 8                          | 0, scaling up to the layer limit |
| Iron Block      | 10                         | 1                                |
| Gold Block      | 12                         | 2                                |
| Emerald Block   | 15                         | 3                                |
| Diamond Block   | 20                         | 4                                |
| Netherite Block | 30                         | 5                                |

The configured pyramid strength is clamped by the layer number. In practical terms, stronger materials can unlock stronger beacon potential, but they are still limited by how many layers the pyramid actually has.

For example:

- An iron layer contributes low strength.
- A diamond layer contributes much more strength.
- A netherite layer has the highest default potential.
- Copper can be used as a beacon base material, but is intentionally much weaker.

## Beacon range

Instead of vanilla's fixed radius table, Beacon Upgrade calculates range from the pyramid's materials.

Each completed layer contributes its configured **effective radius**, and the beacon adds those values together.

For example, with default values:

- One iron layer gives less range than one diamond layer.
- A full netherite pyramid has a much larger range than a full iron pyramid.
- Mixed pyramids produce mixed results depending on the weakest block in each layer.

The effect area is still beacon-like: it expands horizontally around the beacon and upward through the world's build height.

## Payment items determine effect duration

In vanilla, payment items are consumed only to confirm a selection and do not otherwise matter. In Beacon Upgrade, the chosen payment item is stored by the beacon and controls effect duration.

Default payment item durations are:

| Item            | Duration at level 1 | Extra duration per additional level |
|-----------------|---------------------|-------------------------------------|
| Copper Ingot    | 3 seconds           | +1 second                           |
| Iron Ingot      | 11 seconds          | +2 seconds                          |
| Gold Ingot      | 15 seconds          | +3 seconds                          |
| Emerald         | 25 seconds          | +5 seconds                          |
| Diamond         | 45 seconds          | +10 seconds                         |
| Netherite Ingot | 90 seconds          | +15 seconds                         |

The beacon reapplies effects periodically. Better payment items therefore make effects last much longer and give more safety when moving in and out of range.

## Effect selection

Beacon Upgrade replaces the vanilla primary/secondary power layout with a more flexible effect system.

Instead of choosing from a small set of primary effects plus regeneration or level II, the upgraded beacon can offer every effect configured as a valid beacon effect.

Each effect has:

- A minimum pyramid level requirement
- A maximum amplifier based on pyramid level
- A pyramid strength cost per amplifier

This means stronger or more advanced effects require a better pyramid.

### Default available effects

Beacon Upgrade includes the following configured effects by default:

| Effect           | Notes                                                          |
|------------------|----------------------------------------------------------------|
| Speed            | Available early and scales with pyramid level                  |
| Haste            | Available early, capped at amplifier 4                         |
| Jump Boost       | Requires more pyramid investment                               |
| Resistance       | Requires more pyramid investment, capped at amplifier 4        |
| Strength         | Requires a stronger pyramid, capped at amplifier 4             |
| Regeneration     | Available as a normal selectable effect, capped at amplifier 2 |
| Fire Resistance  | Added as a beacon effect                                       |
| Reach            | Added by this mod                                              |
| Nutrition        | Added by this mod                                              |
| Bane of Raiders  | Added by this mod                                              |
| Bane of Phantoms | Added by this mod                                              |
| Flight           | Added by this mod                                              |

The exact strength available depends on the configured effect data and the current pyramid level.

## Multiple effects and amplifier levels

Beacon Upgrade is not limited to one primary and one secondary effect.

Effects can be enabled, disabled, and adjusted in the upgraded beacon screen, as long as the pyramid can support them.

Effect amplifiers are internally stored using Minecraft's normal amplifier values:

| Displayed level | Internal amplifier |
|-----------------|--------------------|
| Level I         | 0                  |
| Level II        | 1                  |
| Level III       | 2                  |
| Level IV        | 3                  |
| Level V         | 4                  |

An effect's maximum amplifier is defined by data map configuration. If the pyramid becomes too small or too weak, selected effects may be clamped or become unavailable until the pyramid is restored.

## Beacon targets

Vanilla beacons only affect players. Beacon Upgrade allows the beacon to target different groups of living entities.

The available target modes are:

| Target mode | Default behaviour            |
|-------------|------------------------------|
| Players     | Affects players              |
| Pets        | Affects owned entities       |
| Friends     | Affects villagers and golems |
| Animals     | Affects animals              |

Hostile mobs are not affected by these default checks. Neutral mobs that are currently hostile towards players are also excluded.

Target groups can be extended with entity type tags, allowing data packs to add more entity types to each target category.

## New mod effects

Beacon Upgrade adds several effects intended for beacon use.

### Reach

Increases block interaction range.

### Nutrition

A beneficial effect related to food and hunger management.

### Bane of Raiders

A beacon effect intended to help against raid-related threats.

### Bane of Phantoms

A beacon effect intended to help against phantoms.

### Bane of Traders

Registered by the mod for trader-related mechanics.

### Flight

Provides flight where supported by the current mod loader/platform implementation.

## Summary for players

If you are used to vanilla beacons, the main things to remember are:

- Beacon materials are no longer cosmetic.
- Better materials provide more strength and range.
- Five-layer pyramids are supported.
- Payment items affect effect duration.
- More effects can be selected and upgraded.
- Beacons can target players, pets, friendly mobs, or animals.
- Data packs can customise almost everything.

## Data pack configuration

Beacon Upgrade is heavily data-driven. Server owners and pack makers can change much of the beacon system without editing code.

The mod uses tags and [data maps](https://docs.neoforged.net/docs/resources/server/datamaps/) extensively which can be configured using custom data packs.

## Tags

### Beacon base blocks

Beacon Upgrade still respects the vanilla beacon base block tag:
```text
data/minecraft/tags/block/beacon_base_blocks.json
```
Blocks in this tag can be used in beacon pyramids. If a block is in this tag but has no custom Beacon Upgrade data map entry, it uses the default base block values:

- Pyramid strength: `1`
- Effective radius: `10`

Beacon Upgrade adds copper blocks to this tag by default.

Example:
```json
{
    "values": [
        "minecraft:iron_block",
        "minecraft:gold_block",
        "minecraft:emerald_block",
        "minecraft:diamond_block",
        "minecraft:netherite_block",
        "#minecraft:copper"
    ]
}
```
### Beacon payment items

Beacon Upgrade also respects the vanilla beacon payment item tag:
```text
data/minecraft/tags/item/beacon_payment_items.json
```
Items in this tag can be used as payment items. If an item is in this tag but has no custom Beacon Upgrade data map entry, it uses the default payment duration:

- 11 seconds at level 1
- +2 seconds per additional pyramid level

Beacon Upgrade adds copper ingots to this tag by default.

Example:
```json
{
    "values": [
        "minecraft:iron_ingot",
        "minecraft:gold_ingot",
        "minecraft:emerald",
        "minecraft:diamond",
        "minecraft:netherite_ingot",
        "minecraft:copper_ingot"
    ]
}
```
### Unaltered beacons

Beacon Upgrade provides this block tag:
```text
data/beaconupgrade/tags/block/unaltered_beacons.json
```
Blocks in this tag are excluded from the upgraded beacon behaviour and remain unchanged.

This is mainly useful for compatibility with other mods that add beacon-like blocks and should not use Beacon Upgrade's replacement logic.

Example:
```json
{
    "values": [
        "examplemod:special_beacon"
    ]
}
```
### Beacon target entity tags

Beacon target modes can be extended with entity type tags:
```text
data/beaconupgrade/tags/entity_type/player_beacon_targets.json
data/beaconupgrade/tags/entity_type/pet_beacon_targets.json
data/beaconupgrade/tags/entity_type/friend_beacon_targets.json
data/beaconupgrade/tags/entity_type/animal_beacon_targets.json
```
If an entity type is included in one of these tags, it is considered a valid target for that beacon target mode.

Example:
```json
{
    "values": [
        "minecraft:villager",
        "minecraft:iron_golem"
    ]
}
```
## [Data maps](https://docs.neoforged.net/docs/resources/server/datamaps/)

Beacon Upgrade defines three data maps:

| Data map                             | Registry    | Purpose                                |
|--------------------------------------|-------------|----------------------------------------|
| `beaconupgrade:beacon_base_blocks`   | Blocks      | Configures pyramid strength and radius |
| `beaconupgrade:beacon_payment_items` | Items       | Configures effect duration             |
| `beaconupgrade:beacon_level_effects` | Mob effects | Configures available beacon effects    |

## Configuring beacon base blocks

Path:
```text
data/beaconupgrade/data_maps/block/beacon_base_blocks.json
```
Format:
```json
{
    "values": {
        "minecraft:diamond_block": {
            "effective_radius": 20.0,
            "pyramid_strength": {
                "type": "beaconupgrade:clamped",
                "min": 0.0,
                "max": {
                    "type": "minecraft:linear",
                    "base": 1.0,
                    "per_level_above_first": 1.0
                },
                "value": 4.0
            }
        }
    }
}
```
Fields:

| Field              | Meaning                                                                      |
|--------------------|------------------------------------------------------------------------------|
| `effective_radius` | Range contribution from this block when used in a completed pyramid layer    |
| `pyramid_strength` | Strength contribution from this block when used in a completed pyramid layer |

Both fields use Minecraft's level-based value system. The level passed into the calculation is the pyramid layer number.

### Simple constant base block
```json
{
    "values": {
        "minecraft:amethyst_block": {
            "effective_radius": 18.0,
            "pyramid_strength": 3.0
        }
    }
}
```
This makes amethyst blocks valid as configured beacon base blocks with 18 radius and 3 pyramid strength.

Remember that for the block to count as a beacon base, it should either be in the vanilla `minecraft:beacon_base_blocks` block tag or have a valid Beacon Upgrade data map entry.

## Configuring payment items

Path:
```text
data/beaconupgrade/data_maps/item/beacon_payment_items.json
```
Format:
```json
{
    "values": {
        "minecraft:diamond": {
            "duration_in_seconds": {
                "type": "minecraft:linear",
                "base": 45.0,
                "per_level_above_first": 10.0
            }
        }
    }
}
```
Fields:

| Field                 | Meaning                                                              |
|-----------------------|----------------------------------------------------------------------|
| `duration_in_seconds` | How long effects from this payment item last, based on pyramid level |

### Simple custom payment item
```json
{
    "values": {
        "minecraft:echo_shard": {
            "duration_in_seconds": {
                "type": "minecraft:linear",
                "base": 60.0,
                "per_level_above_first": 12.0
            }
        }
    }
}
```
This makes echo shards provide 60 seconds at pyramid level 1, then 12 additional seconds per level after that.

## Configuring beacon effects

Path:
```text
data/beaconupgrade/data_maps/mob_effect/beacon_level_effects.json
```
Format:
```json
{
    "values": {
        "minecraft:speed": {
            "max_amplifier": {
                "type": "minecraft:linear",
                "base": 0.0,
                "per_level_above_first": 1.0
            },
            "strength_per_amplifier": {
                "type": "minecraft:linear",
                "base": 1.0,
                "per_level_above_first": 1.0
            }
        }
    }
}
```
Fields:

| Field                    | Meaning                                                   |
|--------------------------|-----------------------------------------------------------|
| `max_amplifier`          | Highest effect amplifier allowed at a given pyramid level |
| `strength_per_amplifier` | Pyramid strength cost for each amplifier level            |

An effect is considered available when its `max_amplifier` reaches at least `0`, which corresponds to Level I.

### Example: effect unlocked later
```json
{
    "values": {
        "minecraft:fire_resistance": {
            "max_amplifier": {
                "type": "minecraft:linear",
                "base": -2.0,
                "per_level_above_first": 1.0
            },
            "strength_per_amplifier": {
                "type": "minecraft:linear",
                "base": 3.0,
                "per_level_above_first": 3.0
            }
        }
    }
}
```
This means:

| Pyramid level | Max amplifier | Available?     |
|---------------|---------------|----------------|
| 1             | -2            | No             |
| 2             | -1            | No             |
| 3             | 0             | Yes, Level I   |
| 4             | 1             | Yes, Level II  |
| 5             | 2             | Yes, Level III |

### Example: clamped maximum amplifier
```json
{
    "values": {
        "minecraft:haste": {
            "max_amplifier": {
                "type": "beaconupgrade:clamped",
                "min": -1.0,
                "max": 4.0,
                "value": {
                    "type": "minecraft:linear",
                    "base": 0.0,
                    "per_level_above_first": 1.0
                }
            },
            "strength_per_amplifier": {
                "type": "minecraft:linear",
                "base": 1.0,
                "per_level_above_first": 1.0
            }
        }
    }
}
```
This allows Haste to scale with pyramid level, but never beyond amplifier 4, which corresponds to Haste V.

## Level-based values

Beacon Upgrade supports Minecraft's normal level-based value formats and adds:
```json
{
    "type": "beaconupgrade:clamped",
    "min": 0.0,
    "max": 5.0,
    "value": {
        "type": "minecraft:linear",
        "base": 1.0,
        "per_level_above_first": 1.0
    }
}
```
The clamped value calculates the nested `value`, then restricts the result between `min` and `max`.

This is useful for making values scale with pyramid level while preventing them from becoming too weak or too strong.

## Compatibility notes

Beacon Upgrade replaces the vanilla beacon block entity and menu behaviour for normal beacons. Mods or data packs that change beacon mechanics may need compatibility adjustments.

Use the `beaconupgrade:unaltered_beacons` block tag for beacon-like blocks that should not be upgraded.
