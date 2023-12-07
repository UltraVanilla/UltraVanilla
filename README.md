# Legacy UltraVanilla plugin

Plugin that UltraVanilla uses. Will be rewritten in the future.

## Build

```bash
sbt assembly
```

## License

Copyright © 2020-2023 UltraVanilla contributors <br> Copyright © 2020-2023 lordpipe <br> Copyright © 2019-2021 Akoot

This program is free software: you can redistribute it and/or modify it under the terms of the [GNU Affero General Public License](LICENSE) as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but **without any warranty**; without even the implied warranty of **merchantability** or **fitness for a particular purpose**. See the GNU Affero General Public License for more details.

Please read the license carefully before using this project; failure to comply with the licensing terms is copyright infringement. Below is a summary of its terms.

You are permitted:

> ✅ **Unlimited commercial and personal use** ([§2](https://www.gnu.org/licenses/agpl-3.0.en.html#section2)) <br>
> ✅ **Distribution** ([§2](https://www.gnu.org/licenses/agpl-3.0.en.html#section2), [§4](https://www.gnu.org/licenses/agpl-3.0.en.html#section4), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6)) <br>
> ✅ **Modification** ([§2](https://www.gnu.org/licenses/agpl-3.0.en.html#section2), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5))<br>
> ✅ **Patent use** ([§11](https://www.gnu.org/licenses/agpl-3.0.en.html#section11))<br>

You must:

> 🔲 **Disclose source when distributing** ([§4](https://www.gnu.org/licenses/agpl-3.0.en.html#section4), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6)) <br>
> 🔲 **Disclose source when making the software functionality accessible via a networked protocol** ([§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section6), [§13](https://www.gnu.org/licenses/agpl-3.0.en.html#section13)) <br>
> 🔲 **Retain the license and copyright notice** ([§4](https://www.gnu.org/licenses/agpl-3.0.en.html#section4), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6)) <br>
> 🔲 **Use the same license** ([§2](https://www.gnu.org/licenses/agpl-3.0.en.html#section2), [§10](https://www.gnu.org/licenses/agpl-3.0.en.html#section10)) <br>
> 🔲 **State changes** ([§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5)) <br>
> 🔲 **Include build and install instructions** ([§1](https://www.gnu.org/licenses/agpl-3.0.en.html#section1), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6))

You may not:

> ❌ **Distribute without disclosing source** ([§4](https://www.gnu.org/licenses/agpl-3.0.en.html#section4), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6)) <br>
> ❌ **Make the software functionality accessible via a networked protocol without disclosing source** ([§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section6), [§13](https://www.gnu.org/licenses/agpl-3.0.en.html#section13)) <br>
> ❌ **Distribute with license and copyright notices removed** ([§4](https://www.gnu.org/licenses/agpl-3.0.en.html#section4), [§5](https://www.gnu.org/licenses/agpl-3.0.en.html#section5), [§6](https://www.gnu.org/licenses/agpl-3.0.en.html#section6)) <br>
> ❌ **Sublicense** ([§2](https://www.gnu.org/licenses/agpl-3.0.en.html#section2), [§10](https://www.gnu.org/licenses/agpl-3.0.en.html#section10)) <br>
> ❌ **Apply technological measures to prevent your users from modifying or copying this software** ([§3](https://www.gnu.org/licenses/agpl-3.0.en.html#section3)) <br>
> ❌ **Use the software while engaging in patent troll litigation against our users and copyright holders** ([§10](https://www.gnu.org/licenses/agpl-3.0.en.html#section10)) <br>
> ❌ Additional terms under AGPLv3 ([§7(e)](https://www.gnu.org/licenses/agpl-3.0.en.html#section7)) (restrictions on trademark usage): <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **You may not use this project's trademarks in conjunction with a cryptocurrency or blockchain project**

This license does not come with:

> ❌ **Any warranty not otherwise mentioned** ([§15](https://www.gnu.org/licenses/agpl-3.0.en.html#section15), [§16](https://www.gnu.org/licenses/agpl-3.0.en.html#section16))

### How do I comply with networked code copyleft in a game?

If you are running modified versions of this software on a server with a game like Minecraft, Minetest, or Hytale, you must provide source code to your users. Here are some recommended ways to do this:

**Method 1:** Near your server contact email address

Mojang requires that all servers must include a contact email address. Include a link to your modified source code close to this.

**Method 2:** `/plugins` and `/version` commands

Paper servers include a `/plugins` command that shows a list of plugins, and a `/version` command showing plugin details. You may include `name`, `authors`, `website` properties in `plugin.yml` to rename the plugin to something unique, and publish your source code publicly under the provided URL.

**Method 3:** Ingame sign

Include a sign near the spawn location of your server that mentions how to acquire the source code for your modified plugin.

### Contributing

By making a contribution to this project, I certify that:

- (a) The contribution was created in whole or in part by me and I have the right to submit it under a suitable open source license; or

- (b) The contribution is based upon previous work that, to the best of my knowledge, is covered under an appropriate open source license and I have the right under that license to submit that work with modifications, whether created in whole or in part by me, under the same open source license (unless I am permitted to submit under a different license), as indicated in the file; or

- (c) The contribution was provided directly to me by some other person who certified (a), (b) or (c) and I have not modified it.

- I understand and agree that this project and the contribution are public and that a record of the contribution is maintained indefinitely and may be redistributed consistent with this project or the open source license(s) involved.
